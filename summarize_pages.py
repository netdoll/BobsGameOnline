import os
import glob

def run():
    files = glob.glob('bobcoin/frontend/src/pages/*.jsx')
    res = {}
    for f in files:
        with open(f, 'r') as fp:
            res[os.path.basename(f)] = fp.read()[:500]
    return res

print(run())
