"""Fail CI if the distributable is missing classes or Fabric metadata."""
import json
from pathlib import Path
from zipfile import ZipFile

properties = dict(
    line.split("=", 1)
    for line in Path("gradle.properties").read_text().splitlines()
    if "=" in line and not line.startswith("#")
)
jar = Path("build/libs") / f"{properties['mod_id']}-{properties['mod_version']}.jar"
with ZipFile(jar) as archive:
    assert archive.testzip() is None, "Corrupt JAR"
    metadata = json.loads(archive.read("fabric.mod.json"))
    assert metadata["id"] == properties["mod_id"]
    assert metadata["version"] == properties["mod_version"], "Unexpanded version"
    assert archive.read(metadata["accessWidener"]), "Missing access widener"
    for entrypoints in metadata["entrypoints"].values():
        for entrypoint in entrypoints:
            assert entrypoint.replace(".", "/") + ".class" in archive.namelist(), entrypoint
    assert "assets/traderefresh/lang/en_us.json" in archive.namelist()
    assert f"LICENSE_{properties['mod_id']}" in archive.namelist()
print(f"Verified {jar}: valid archive, metadata, entrypoints, access widener and assets")
