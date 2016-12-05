#!/usr/bin/env python
# encoding: utf-8

from os.path import splitext, join
import codecs
import traceback
import argparse
from shutil import copyfile
import fileinput
from pandas import read_csv
import os
from collections import defaultdict 
from numpy import std, mean, median

FIELD_NAMES = ["word", "cid","keyword","cluster"]
FIELD_NAMES_OUT = ["word", "cid","cluster","isas"]
CHUNK_LINES = 500000
LIST_SEP_IN="  "
LIST_SEP_OUT=","
MIN_CLUSTER="5"


def add_header(input_fpath, header):
    for line in fileinput.input(files=[input_fpath], inplace=True):
        if fileinput.isfirstline():
            print header
        print line,


def try_remove(fpath):
    if exists(fpath):
        os.remove(fpath)


def exists(dir_path):
    return os.path.isdir(dir_path) or os.path.isfile(dir_path)


def postprocess(ddt_fpath, output_fpath, filtered_fpath, min_size, list_sep):
    print "Input DDT:", ddt_fpath
    print "Output DDT:", output_fpath
    print "Filtered out DDT clusters:", filtered_fpath
    print "Min size:", min_size
    print "Input list sep.: '%s'" % list_sep
    
    min_size = int(min_size)
    
    with codecs.open(output_fpath, "w", encoding="utf-8") as output, codecs.open(filtered_fpath, "w", encoding="utf-8") as filtered:
        reader = read_csv(ddt_fpath, encoding="utf-8", delimiter="\t", error_bad_lines=False,
            iterator=True, chunksize=CHUNK_LINES, doublequote=False, quotechar=u"\u0000", header=None, names=FIELD_NAMES)
        num = 0
        selected_num = 0
        senses_num = defaultdict(int)

        for i, chunk in enumerate(reader):
            # print header
            if i == 0: output.write("\t".join(FIELD_NAMES_OUT) + "\n")
            chunk.fillna('')
            
            # rows
            for j, row in chunk.iterrows():
                num += 1
                
                # filters
                cluster = row.cluster.split(list_sep)
                if len(cluster) < min_size:
                    filtered.write("%s\t%s\t%s\n" % (row.word, row.cid, LIST_SEP_OUT.join(cluster)))
                    continue
                output.write("%s\t%s\t%s\t\n" % (row.word, row.cid, LIST_SEP_OUT.join(cluster)))
                selected_num += 1
                senses_num[row.word] += 1

        print "# output clusters: %d of %d (%.2f %%)" % (selected_num, num, float(selected_num)/num*100.)
        values = senses_num.values()
        print "average number of senses: %.2f +- %.3f, median: %.3f" % (mean(values), std(values), median(values))


def main():
    parser = argparse.ArgumentParser(description='Postprocess word sense induction file.')
    parser.add_argument('ddt', help='Path to a csv file with a DDT: "word<TAB>cid<TAB>cluster" w/o header. Here <cluster> is "word:sim<list-sepatator>word:sim..."')
    parser.add_argument('--min_size', help='Minimum cluster size. Default -- 5.', default=MIN_CLUSTER)
    parser.add_argument('--list_sep_in', help='List seperator. Default -- "%s".' % LIST_SEP_IN, default=LIST_SEP_IN)
    args = parser.parse_args()

    output_fpath = splitext(args.ddt)[0] + "-minsize" + args.min_size + ".csv"
    filtered_fpath = splitext(args.ddt)[0] + "-minsize" + args.min_size + "-filtered.csv"

    postprocess(args.ddt, output_fpath, filtered_fpath, args.min_size, args.list_sep_in)

if __name__ == '__main__':
    main()
