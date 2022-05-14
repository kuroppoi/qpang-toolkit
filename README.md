# QPang Toolkit
[![build](https://github.com/kuroppoi/qpang-toolkit/actions/workflows/gradle.yml/badge.svg)](https://github.com/kuroppoi/qpang-toolkit/actions)

QPang Toolkit (or QToolkit for short) is a collection of tools for reading, modifying and writing QPang files.\
Be aware that this tool is not yet entirely complete, and that some features are still missing (but planned!)\
That said, feel free to browse around!

## How to set up?
### Prerequisites
- Java 8 or newer

The latest release can be downloaded from [here.](https://github.com/kuroppoi/qpang-toolkit/releases/latest)\
If you want to build from source, download or clone this repository and run `gradlew dist` in the root directory.\
The output jar will be located in `build/libs` after the build has finished.

## What's included?
A list of planned features can be found at the [projects page.](https://github.com/kuroppoi/qpang-toolkit/projects)\
Here's what's currently included in the toolkit:
- A neat, simple (work-in-progress) graphical user interface
- Tools for decrypting and encrypting `.conf` and `.dat` files
- Tools for reading and writing `.pkg` files
- Tools for reading and writing `.pack` files
- Tools for reading and writing `.mesh` files
- Tools for reading and writing `.collision` files
- Tools for parsing `.scene` files
- Tools for parsing `.material` files
- Converters for converting meshes and collisions to `.obj` and vice versa
- Converter for converting `.material` files to `.mtl`
- A simple in-memory file system utility
