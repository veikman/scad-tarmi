# Change log
This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
### Fixed
- Error functions no longer reduce measurements to below zero.
- Error functions now return zero for a measurement of zero, whereas before,
  they would throw an `ArithmeticException`.

## [Version 0.8.0]
### Added
- A “maybe” wrapper for `offset`.

## [Version 0.7.0]
### Added
- Support for one-dimensional numeric use of error-fn closures.
- Support for modifying local error by a coefficient.

## [Version 0.6.0]
### Added
- “maybe” wrappers for `projection`, `project` and `cut`.

## [Version 0.5.0]
### Changed
- The `threaded` module has been marked as deprecated for all purposes. Use
  the `scad-klupe` library instead.

### Added
- In addition to its previous behaviour, `maybe/translate` will now absorb
  child elements that are single translations, further simplifying output.

## [Version 0.4.0]
### Added
- An optional `point-type` parameter to `threaded/bolt`. It takes one
  value, `:cone`, and leaves the point of the bolt flat when omitted.
- A “maybe” wrapper for `hull`.

## [Version 0.3.0]
### Added
- A `util` namespace with `loft` and `radiate` functions.
- A `flex` namespace with functions that apply their namesakes from the
  `reckon` or `maybe` modules, upon inspection of their arguments.
- “maybe” wrappers for `mirror` and `polyhedron`.
- A `mean` function.
- A `unit-circle-point-coord` function.
- Some registered Clojure specs for Cartesian coordinates.

## [Version 0.2.0]
### Added
- A `reckon` module for reasoning about a model.
- An `abs` function for absolute values.

## Version 0.1.0
### Added
- “maybe” wrappers for `rotate`, `scale`, `translate`, `union`,
  `intersection`, `difference` and `polygon`.
- A couple of functions for DFM.
- Threaded fasteners and a script showcasing a selection of these.
- `long-hex-diagonal` for computing the long diagonal of a hexagon
  from the short diagonal.
- A handful of constants and abbreviations of dubious value:
  √, sin, cos, π and τ.

[Unreleased]: https://github.com/veikman/scad-tarmi/compare/v0.8.0...HEAD
[Version 0.8.0]: https://github.com/veikman/scad-tarmi/compare/v0.7.0...v0.8.0
[Version 0.7.0]: https://github.com/veikman/scad-tarmi/compare/v0.6.0...v0.7.0
[Version 0.6.0]: https://github.com/veikman/scad-tarmi/compare/v0.5.0...v0.6.0
[Version 0.5.0]: https://github.com/veikman/scad-tarmi/compare/v0.4.0...v0.5.0
[Version 0.4.0]: https://github.com/veikman/scad-tarmi/compare/v0.3.0...v0.4.0
[Version 0.3.0]: https://github.com/veikman/scad-tarmi/compare/v0.2.0...v0.3.0
[Version 0.2.0]: https://github.com/veikman/scad-tarmi/compare/v0.1.0...v0.2.0
