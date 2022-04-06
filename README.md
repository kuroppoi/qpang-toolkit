# QPang Toolkit

[![build](https://github.com/kuroppoi/qpang-toolkit/actions/workflows/gradle.yml/badge.svg)](https://github.com/kuroppoi/qpang-toolkit/actions)

QPang Toolkit (or QToolkit for short) is a collection of tools for reading, modifying and writing QPang files.\
Be aware that this tool is not yet entirely complete, and that some features are still missing (but planned!)\
That said, feel free to browse around!

## Features
Here's a quick rundown of all the things QToolkit is capable of:
- Decrypting and encrypting `.conf` files
- Reading, modifying and writing `.pkg` files as well as exporting them as directories (and back)
- Reading, modifying and writing `.pack` files as well as exporting them as directories (and back)
- Reading, modifying and writing `.mesh` files as well as converting them (or separate meshes) to `.obj` (and back)
- Reading, modifying and writing `.collision` files (both BSP and legacy versions) as well as converting them (or separate collisions) to `.obj` (and back)
- Parsing `.scene` files and using them to transform mesh positions when converting `.mesh` files to `.obj`
- Parsing `.material` files and using them to generate `.mtl` files

## Work in progress
Here's a list of (some of the) features that are still being worked on:
- Tools for reading, modifying and writing `.ona` files as well as converting them to `.obj` (and back)
- Tools relevant for `.animation` files and `.onp` files
- A parser for `.conf` files
