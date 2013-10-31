#nzbsplit
Splits NZB files into smaller parts.

NZB files can be split into a given number of files or they can be split into parts that are no larger than a given size.

#Usage
```
nzbsplit [-s <MAX_SIZE>|-n <NUM_SPLIT>] [-hv] <NZB_FILE>

  -h, --help                       Displays this message then exits
  -n, --number <NUM_SPLIT>         Split <NZB_FILE> into at most <NUM_SPLIT> NZB parts
  -s, --max-size-split <MAX_SIZE>  Split <NZB_FILE> into at most <MAX_SIZE> NZB parts
  -v, --verbose                    Prints information about the split NZB files
```
#Requirements
* Java 7 (JRE 1.7)
* Apache Ant (tested with 1.9.1)

#Build
```
cd nzbsplit/
ant jar
```

#Run
```
/path/to/java -jar dist/nzbsplit.jar OPTIONS 
```

#Files
The output nzb files are created in the the current working directory. The file names are generated from the input nzb file name and are given a part number e.g. input.nzb would be split into files input_0.nzb, input_1.nzb etc...The part number will padded with zeroes to accomodate the amount of files