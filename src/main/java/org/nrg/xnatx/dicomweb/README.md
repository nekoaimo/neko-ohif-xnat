## Notes
### Offset Tables
* [PS3.5 8 Encoding of Pixel, Overlay and Waveform Data](https://dicom.nema.org/medical/dicom/current/output/chtml/part05/chapter_8.html)
* [8.2 Native or Encapsulated Format Encoding](https://dicom.nema.org/medical/dicom/current/output/chtml/part05/sect_8.2.html)
</br>
A frame may be entirely contained within a single fragment, or may span multiple fragments.
Detect fragmentation of frames by comparing the number of fragments (the number of Items minus one for the Basic Offset Table) with the number of frames
* [A.4 Transfer Syntaxes For Encapsulation of Encoded Pixel Data - Basic Offset Table](https://dicom.nema.org/medical/dicom/current/output/chtml/part05/sect_A.4.html)
</br>
Basic Offset Table (BOT):
A table of 32-bit pointers to individual frames of an encapsulated Multi-frame Image.
</br>
Extended Offset Table (EOT):
A table of 64-bit pointers to individual frames of an encapsulated Multi-frame Image.

## ToDo
* supplement a default CharacterSet when SpecificCharacterSet is missing


