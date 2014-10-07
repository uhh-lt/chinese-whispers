This is an implementation of the Chinese Whispers graph clustering algorithm. For an introduction
or if you need to reference the algorithm, use this paper:
http://wortschatz.uni-leipzig.de/~cbiemann/pub/2006/BiemannTextGraph06.pdf

It is specifically used for word sense induction (WSI).

You can compile the code using Maven, and run the WSI algorithm from the command line.

Here's a quickstart guide:
```bash
git clone https://github.com/johannessimon/chinese-whispers.git
cd chinese-whispers && mvn package
java -cp target/chinese-whispers.jar de.tudarmstadt.lt.wsi.WSI
````

You may also of course use the CW algorithm directly from your code.