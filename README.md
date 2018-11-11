# `scad-tarmi`: Commonplace items for `scad-clj`

This is a Clojure library of miscellaneous abstractions and conveniences for
use with `scad-clj`. The contents include functions for drawing threaded
fasteners: Metric nuts and bolts etc.

The Lojban word “tarmi” refers to a conceptual shape.

## Usage

### Threaded fasteners

If in your `ns` declaration you `(:require [scad-tarmi.threaded :refer [nut]])`,
you can then call `(nut :iso-size 6)` for an ISO 262 M6 hex nut. Its height
and diameter are inferred from the standard, unless you pass overrides.

Models of fasteners, as produced by this library, are neither perfectly
accurate with respect to standards such as ISO 262, nor engineered for ease
of printing. Their main purpose is for use in negatives: They are relatively
simple shapes used to carve out screw holes, nut pockets etc. in 3D-printable
designs. These holes would then be filled by ordinary steel nuts and bolts
in the assembly of your product.

If you do intend to model an M6 nut to carve out negative space for a real nut,
the call would be `(nut :iso-size 6 :negative true)`.

Either way, you will need to use `scad-clj` to produce OpenSCAD code for the
object.

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
