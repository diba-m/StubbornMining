#! /bin/bash

timestamp() {
  date +"%T"
}

 for i in `seq 1 100`;
        do
                echo 'copy file nr.:' $i
                sleep 1
                cp test_file.pdf '/Volumes/zpool-docker/test_file_'$(date +%s%N)'.pdf'
        done
