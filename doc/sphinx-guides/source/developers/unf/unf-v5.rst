UNF Version 5
================================

**Important Update:**

**UNF Version 5 has been in use by the Dataverse project since 2009. It was built into every version of the DVN, starting with 2.0 and up to 3.6.2. However, some problems were recently found in that implementation. Namely, in certain cases data normalization is not implemented fully to the spec. UNF signatures it generates are still reasonably strong statistically; however, this means that at least some of our signatures are not independently verifiable. I.e., if somebody fully implements their own version of UNF calculator, for certain datasets it would calculate signatures different from those generated by the DVN. Unless of course they implement it with the exact same bugs as ours.**

**To address this, the Project is about to release UNF Version 6. The release date is still being discussed. It may coincide with the release of Dataverse 4.0. Alternatively, the production version of DVN 3.6.3 may get upgraded to use UNF v6 prior to that. This will be announced shortly. In the process, we are solving another problem with UNF v5 - this time we've made an effort to offer very implementer-friendly documentation that describes the algorithm fully and unambiguously. So if you are interested in implementing your own version of a UNF calculator, (something we would like to encourage!) please disregard the document below and go directly to the Version 6 documentation.**

**Going forward, we are going to offer a preserved version of the Version 5 library and, possibly, an online UNF v5 calculator, for the purposes of validating vectors and data sets for which published Version 5 UNFs exist.**

-----

Version 5 of the UNF algorithm is used by the Dataverse software version 2.0 and up to 3.6.2, and is implemented in Java code. As in version 3, this algorithm is used on digital objects containing vectors of numbers, vectors of character strings, data sets comprising such vectors, and studies comprising one or more such data sets. Version 5 adds normalization forms for date, time, duration, bitstring and logical values, in addition to those used for numeric and character values in version 3.

The UNF V5 algorithm applied to the content of a data set or study is as follows:

1.  Calculate a UNF for each element as follows:

 • Round elements in a numeric vector to k significant digits using the IEEE 754 round towards nearest, ties to even rounding mode. The default value of k is seven, the maximum expressible in single-precision floating point calculations.

 • Calculate the UNF for vectors of character strings as above, except truncate to k characters and the default value of k is 128.

 • Normalize boolean values to numeric values of either 0, 1, or missing. No rounding is applied.

 • Normalize bit fields by converting to big-endian form, truncating all leading empty bits, aligning to a byte boundary by re-padding with leading zero bits, and base64 encoding to form a character string representation. No rounding is applied, and missing values are represented by three null bytes.

 • Normalize time, date, and durations based on a single, unambiguous representation selected from the many described in the ISO 8601 standard.

 • Convert calendar dates to a character string of the form YYYY-MM-DD. Partial dates in the form YYYY or YYYY-MM are permitted.

 Time representation is based on the ISO 8601 extended format, hh:mm:ss.fffff. When .fffff represents fractions of a second, it must contain no trailing (non-significant) zeroes, and is omitted if valued at zero. Other fractional representations, such as fractional minutes and hours, are not permitted. If the time zone of the observation is known, convert the time value to the UTC time zone and append a ”Z” to the time representation.

 Format elements that comprise a combined date and time by concatenating the (full) date representation, “T”, and the time representation. Do not use partial date representations for combined date and time values.

 For type-specific approximation, delete the entire component of the time, date, or combined time-date in the following order: fractional seconds, seconds, minutes, hours, day, time zone indicator (if any), and month.

 Represent durations by using two date-time values, formatted as defined previously, and separated by a solidus (“/”), where each [n] represents the number of years, months, dates, hours, minutes, and seconds (respectively) in the duration.

 Fractional values of seconds (only) are permitted in the form of nnn.fffff. Where n=0, the “0” is required. All other leading and trailing zeroes, fractional hours and minutes, and truncated values are prohibited. Use durations only where the actual start time is not known, otherwise use a time interval must be used.

2.  Convert each vector element to a character string in exponential notation, omitting noninformational zeros.

 If an element is missing, represent it as a string of three null characters.

 If an element is an IEEE 754, nonfinite, floating-point special value, represent it as the signed, lowercase, IEEE minimal printable equivalent (that is, +inf, -inf, or +nan).

 Each character string comprises the following:

 • A sign character.

 • A single leading digit.

 • A decimal point.

 • Up to k-1 digits following the decimal, consisting of the remaining k-1 digits of the number, omitting trailing zeros.

 • A lowercase letter "e."

 • A sign character.

 • The digits of the exponent, omitting trailing zeros.

 For example, the number pi at five digits is represented as -3.1415e+, and the number 300 is represented as the string +3.e+2.


3.  Terminate character strings representing nonmissing values with a POSIX end-of-line character.

4.  Encode each character string with `Unicode bit encoding <http://www.unicode.org/versions/Unicode4.0.0/>`_. Version 5 uses UTF-8.

5.  Combine the vector of character strings into a single sequence, with each character string separated by a POSIX end-of-line character and a null byte.

6.  Compute a hash on the resulting sequence using the standard `SHA256 <http://csrc.nist.gov/publications/fips/fips180-2/fips180-2withchangenotice.pdf>`_ hashing algorithm. The resulting hash is base64 encoded to support readability.

7.  Calculate the UNF for each lower-level data object, using a consistent UNF version and level of precision across the individual UNFs being combined.

8.  Sort the `base64 <http://www.ietf.org/rfc/rfc3548.txt>`_ representation of UNFs in POSIX locale sort order.

9.  Apply the UNF algorithm to the resulting vector of character strings using k at least as large as the length of the underlying character string.

10.  Combine UNFs from multiple variables to form a single UNF for an entire data frame, and then combine UNFs for a set of data frames to form a single UNF that represents an entire research study.

Learn more: 
Micah Altman. 2008. "A Fingerprint Method for Verification of Scientific Data", in Advances in Systems, Computing Sciences and Software Engineering (Proceedings of the International Conference on Systems, Computing Sciences and Software Engineering 2007), Springer Verlag. (`Web site <http://micahaltman.com/>`_
)