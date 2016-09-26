# Popis parametrů jednotlivých experimentů

[TOC]

## Evokované potenciály (ERP)

 - Out - Zde bude popis... Hodnota se zadává v `ms`.
 - Wait - Zde bude popis... Hodnota se zadává v `ms`.
 - Edge - Hrana, na kterou se budou zaznamenávat hodnoty.
  - Leading (*Vzestupná*)
  - Falling (*Sestupná*)
 - Random - Zde bude popis...
  - OFF - Zde bude popis...
  - SHORT - Zde bude popis...
  - LONG - Zde bude popis...
  - SHORT LONG - Zde bude popis...

### Parametry jednotlivých výstupů
 - Pulse - Zde bude popis...
  - Up - Doba, po kterou je výstup aktivní. Hodnota se zadává v `ms`.
  - Down - Doba, po kterou je výstup neaktivní. Hodnota se zadává v `ms`.
 - Distribution - Zde bude popis...
  - Value - Rozdělené pravděpodobnosti v `%`. Počet bliknutí výstupu je odvozen z této hodnoty. Mezi n výstupů je rozděleno 100%.
  - Delay - Zde bude popis... Hodnota se zadává v `ms`.
 - Brightness - Nastavuje jas v `%` pro jednotlivé výstupy. Každý výstup ůže mít jinou hodnotu.

## Brain-computer interface (BCI)

### F-VEP
 - Pulse - Zde bude popis...
  - Up - Doba, po kterou je výstup aktivní. Hodnota se zadává v `ms`.
  - Down - Doba, po kterou je výstup neaktivní. Hodnota se zadává v `ms`.
 - Frequency - Frekvence jednotlivých stimulů. Hodnota je v `Hz` z intervalu `<0 - 20>` s krokem `0.5 Hz`.
 - Duty cycle - Určuje délku pulzu při nastavení frekvence. Hodnota se zadává v `%`.
 - Brightness - Nastavuje jas v `%` pro jednotlivé výstupy. Každý výstup ůže mít jinou hodnotu.

### T-VEP
 - Pattern length - Velikost paternu. Hodnota je z intervalu `<1, 16>`.
 - Puls length - Délka pulzu. Hodnota se zadává v `ms`.
 - Time between - Velikost mezery mezi dvěma pulzy. Hodnota se zadává v `ms`.
 - Brightness - Nastavuje jas v `%` pro jednotlivé výstupy.

### C-VEP
 - Puls length - Délka pulzu. Hodnota se zadává v `ms`.
 - Bit shift - Bitový posun jednotlivých paternů od hlavního. Hodnota je z intervalu `<1 - 31>`.
 - Brightness - Nastavuje jas v `%` pro jednotlivé výstupy.

## Reaction experiment (REA)
 - Cycle count - Počet cyklů. Hodnota je z intervalu `<1 - 50>`.
 - Wait
  - Fixed - Zde bude popis... Hodnota se zadává v `ms`.
  - Random - Zde bude popis... Hodnota se zadává v `ms`.
 - Miss time - Zde bude popis... Hodnota se zadává v `ms`.
 - Brightness - Nastavuje jas v `%` pro jednotlivé výstupy.
 - On fail - Co se stane, když osoba nestihne zaregovat.
  - Wait - Počkat.
  - Continue - Pokračovat v experimentu.
 - Gender
  - Male
  - Female
 - Age - Věk testované osoby. Hodnota je v `cm` z intervalu `<1, 99>`.
 - Height - Výška testované osoby. Hodnota je v `Kg z intervalu `<1, 255>`.