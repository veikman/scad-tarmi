# `scad-tarmi`: Commonplace items for `scad-clj`

This is a Clojure library of miscellaneous abstractions and conveniences for
use in CAD work with `scad-clj`.

[![Clojars Project](https://img.shields.io/clojars/v/scad-tarmi.svg)](https://clojars.org/scad-tarmi)

The Lojban word *tarmi* refers to a conceptual shape.

## Usage

The [`core`](src/scad_tarmi/core.clj) module is trivial, meant only to reduce
boilerplate.

The [`maybe`](src/scad_tarmi/maybe.clj) module is very simple: It carries
drop-in replacements for `scad-clj` functions that will produce as little
output as possible, for slightly shorter OpenSCAD artefacts.

### Design for manufacturability

The [`dfm`](src/scad_tarmi/dfm.clj) module exposes an `error-fn` function.

This simple function represents a compromise between several concerns in DFM
for ordinary 3D printers. It is based on these assumptions:

* The target printer is correctly calibrated, but the combination of printer
  firmware and slicer software causes printed models to be larger than
  indicated by their blueprint: A measurable error.

* z-level accuracy is beyond software control, as in FDM.

* The size of the error is absolute for a given combination of printer
  nozzle, flow rate, material properties, temperature, cooling method etc.
  Thus, unlike errors due to a process of annealing, the size of the error
  predicted by `error-fn` does not itself vary with the size of the
  printed part, except at very small multiples of the nozzle diameter.

* The size of the error is about twice as big on the inside of a gap in the
  model as it is on the outside. In other words, if the outside diameter of
  a model grows by 0.1 mm in printing, a hole (i.e. negative space) in that
  model, caused by a difference() operation in OpenSCAD, will shrink by
  0.2 mm.

These assumptions, and default values applied in `error-fn`, are based
on tests of a LulzBot TAZ 6, an FDM printer with a 0.5 mm nozzle, running
its default Marlin firmware (version current as of 2018-11), slicing in Cura
(LulzBot edition, v3.2) and printing PLA at 100% flow. Results will vary
with other printers, slicers and materials. Measure the size of your error
with a test print and pass it to `error-fn` to get a compensator.

Lastly, `error-fn` assumes that negative space will be used to fit other parts,
such as threaded fasteners, where dimensions are sensitive. Therefore, negative
space is the primary use case. `error-fn` primarily expects a negative value
as a measurement of error and will assume that any passed nominal measurement
passed to a compensator should be enlarged.

### Threaded fasteners

The [`threaded`](src/scad_tarmi/threaded.clj) module describes threaded
fasteners using the `core`, `maybe` and `dfm` modules.

If in your `ns` declaration you `(:require [scad-tarmi.threaded :refer [nut]])`,
you can then call `(nut :iso-size 6)` for an ISO 262 M6 hex nut. Its height
and diameter are inferred from the standard, unless you pass overrides.
It is internally threaded.

Models of fasteners, as produced by this library, are neither perfectly
accurate with respect to standards, nor engineered for ease of printing.
Their main purpose is to form negative space: Relatively simple shapes used
to carve out screw holes, nut pockets and similar cavities in 3D-printable
designs. These holes would then be filled by ordinary steel nuts and bolts
in the assembly of your product.

If you do intend to model an M6 nut to carve out negative space for a real nut,
the call would be `(nut :iso-size 6 :negative true)`, a less complicated shape.

Either way, you will need to use `scad-clj` to produce OpenSCAD code for the
object. Check out [the showcase](src/showcase/core.clj) for a few examples.

## Acknowledgements

The thread-drawing function (`threaded/thread`) is a reimplementation in
Clojure of a corresponding function in `polyScrewThread_r1.scad`, created by
*aubenc* [at Thingiverse](http://www.thingiverse.com/thing:8796) and released
by the author into the public domain.

## License

Copyright © 2018 Viktor Eikman

This software is distributed under the [Eclipse Public License](LICENSE-EPL),
(EPL) v2.0 or any later version thereof. This software may also be made
available under the [GNU General Public License](LICENSE-GPL) (GPL), v3.0 or
any later version thereof, as a secondary license hereby granted under the
terms of the EPL.
