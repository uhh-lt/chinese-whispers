remove the first line
%s/\t$//gc
%s/\t\([0-9]\+\)\t/\t\1\tword\t/gc
%s/,/  /gc
