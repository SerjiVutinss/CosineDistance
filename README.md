# CosineDistance
Multithreaded Cosine Distance Calculator written in Java

## Overview

Two paths must be input on the command line, the query path and the subject folder path.
The user will then be asked to input the shingle size to be used for creating the file maps.

For each file, a CallableFileMapperService is created and submitted to an executor service.
Once the query file has been mapped and its Future is ready, a CompararerController is then started which compares all mapped files with the query file.

Each file mapper service creates 3 new threads:
  * RunnableReader for reading the file
  * RunnableShingler for shingling the file
  * CallableMapper for mapping the file
  File chunks are read on to a blocking queue in RunnableReader, this blocking queue is then read in RunnableShingler where the shingled chunks are then placed on another blocking queue.
  The shingled chunks are then read from the blocking queue and a Map is created - these maps are passed back to the CosineSimilarityController where they are then compared.
  
The ComparerController starts a new CallableComparator which compares each subject file as it is added to the Map Blocking queue.  The query file Future must be ready before this process starts.

As the maps comparisons are completed, the results are printed to the console.

Finally, a summary is printed.

## UI

The UI has been left intentionally minimal with no validation on user input.  The only additional input required by the user is the shingle size to be used during the shingling process.

I had considered adding both the chunkSize(RunnableReader) and queueCapacity(size of each BlockingQueue used) to the UI but ultimately decided against this.

I had also considered adding a JavaFX UI but also decided against this due to the added and perhaps unneccesary complexity it would add.

## Notes

I am unsure whether the use of this many threads is of any real benefit but since this project was an excercise in threading, I decided to use as many as possible to enhance my understanding of the concepts introduced in the module.

