[![Build Status](https://travis-ci.org/tudarmstadt-lt/chinese-whispers.svg?branch=master)](https://travis-ci.org/tudarmstadt-lt/chinese-whispers) [![Release](https://jitpack.io/v/tudarmstadt-lt/chinese-whispers.svg)](https://jitpack.io/#tudarmstadt-lt/chinese-whispers)

Chinese Whispers
================

This is an implementation of the Chinese Whispers (CW) graph clustering algorithm. For an introduction
or if you need to reference the algorithm, use this paper:
<http://wortschatz.uni-leipzig.de/~cbiemann/pub/2006/BiemannTextGraph06.pdf>.

This project contains implementation of the CW algorithm. You can run it for each ego network of the graph (e.g. to perform word sense induction or for segmenting a social ego network) or perform global clustering of the graph. In the first case, you will obtain one clustering for each node, in the latter case you will obtain one set of clusters per graph. 

## Running from the command line

Compile and run the algorithm in the ego network mode:

```
git clone https://github.com/tudarmstadt-lt/chinese-whispers.git
cd chinese-whispers && mvn package
java -cp target/chinese-whispers.jar de.tudarmstadt.lt.wsi.WSI
```

Run the algorithm in the ego network mode with the default parameters for word sense induction

```
run.sh <distributional-thesaurus.csv>
```

For an example of how to use the CW clustering algorithm for word sense induction, compile the code as shown above and download
example data, like this word similarity graph extracted from a 120-million-lines English news
corpus taken from the JoBimText project:
<http://sourceforge.net/projects/jobimtext/files/data/models/en_news120M_stanford_lemma/LMI_p1000_l200.gz>.

The data is formatted in _ABC_ format, meaning that each row contains an edge of the graph,
and each row contains three columns separated by a whitespace: _from_, _to_, and the _edge weight_.
The provided data should be sorted using, for instance, `sort -u`. Please note that for
undirected graphs each edge should be provided with two rows: _from_, _to_, _weight_
and _to_, _from_, _weight_.

Then run the WSI algorithm on the data (making sure you assign enough memory to the VM):

```bash
java -Xms4G -Xmx4G -cp target/chinese-whispers.jar de.tudarmstadt.lt.wsi.WSI \
     -in /path/to/LMI_p1000_l200.gz -clustering cw -n 100 -N 100 -out test-output.txt
```

The output (in our case test-output.txt) is then formatted as follows:

```
word <TAB> cluster-id <TAB> cluster-label <TAB> cluster-node1 cluster-node2 ...
word <TAB> cluster-id <TAB> cluster-label <TAB> cluster-node1 cluster-node2 ...
...
```

In addition, a default implementation of chinese-whispers for global clustering is available:

```bash
java -Xms4G -Xmx4G -cp target/chinese-whispers.jar de.tudarmstadt.lt.cw.global.CWGlobal \
     -in /path/to/edges.gz -N 1000 -out clusters.csv.gz
```
N limits how many edges are maximum added per node when building the graph


## Using the library from Java code

You may use the CW algorithm directly from your code.


* add the jitpack Maven dependency
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
* add the chinese-whispers dependency
```
	<dependency>
	    <groupId>com.github.tudarmstadt-lt</groupId>
	    <artifactId>AB-Sentiment</artifactId>
	    <version>-SNAPSHOT</version>
	</dependency>
```
* use the ```de.tudarmstadt.lt.cw.global.CWGlobal``` class for global clustering mode and ```de.tudarmstadt.lt.wsi.WSI``` for the ego network clustering mode. 

