#!/usr/bin/env python3

with open('file.txt', 'w') as f:
    for i in range(5000000000):
        f.write(str(i + 1) + '\n');
