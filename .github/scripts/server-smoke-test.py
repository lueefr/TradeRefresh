"""Start the mod on a dedicated Fabric server, then require a clean shutdown."""
import json
import os
from pathlib import Path
import queue
import signal
import subprocess
import threading
import time

run = Path("run")
run.mkdir(exist_ok=True)
# Acceptance applies only to this disposable CI test server.
(run / "eula.txt").write_text("eula=true\n")
generator = json.dumps({
    "biome": "minecraft:plains",
    "layers": [
        {"block": "minecraft:bedrock", "height": 1},
        {"block": "minecraft:dirt", "height": 2},
        {"block": "minecraft:grass_block", "height": 1},
    ],
})
(run / "server.properties").write_text(
    "online-mode=false\nserver-ip=127.0.0.1\nview-distance=2\n"
    "simulation-distance=2\nlevel-type=minecraft:flat\nmax-players=1\n"
    f"generator-settings={generator}\n"
)
process = subprocess.Popen(
    ["./gradlew", "runServer", "--no-configuration-cache", "--console=plain"],
    stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
    text=True, bufsize=1, start_new_session=True,
)
lines = queue.Queue()


def read_output():
    for line in process.stdout:
        lines.put(line)
    lines.put(None)


threading.Thread(target=read_output, daemon=True).start()
started = False
deadline = time.monotonic() + 360
try:
    with Path("server-smoke.log").open("w") as log:
        while True:
            remaining = deadline - time.monotonic()
            if remaining <= 0:
                raise TimeoutError("Server did not start and stop within the time limit")
            try:
                line = lines.get(timeout=remaining)
            except queue.Empty:
                raise TimeoutError("Timed out waiting for the dedicated server") from None
            if line is None:
                break
            print(line, end="", flush=True)
            log.write(line)
            log.flush()
            if not started and 'Done (' in line and 'For help, type "help"' in line:
                started = True
                process.stdin.write("stop\n")
                process.stdin.flush()
                deadline = time.monotonic() + 60
    result = process.wait(timeout=10)
    assert started, "Dedicated server exited before reaching ready state"
    assert result == 0, f"Dedicated server failed with exit code {result}"
    output = Path("server-smoke.log").read_text()
    assert "- traderefresh " in output, "Trade Refresh was not loaded"
    assert "/ERROR]" not in output and "/FATAL]" not in output, "Server logged a runtime error"
    print("Dedicated server smoke test passed: startup and clean shutdown")
finally:
    if process.poll() is None:
        os.killpg(process.pid, signal.SIGTERM)
        try:
            process.wait(timeout=10)
        except subprocess.TimeoutExpired:
            os.killpg(process.pid, signal.SIGKILL)
            process.wait()
