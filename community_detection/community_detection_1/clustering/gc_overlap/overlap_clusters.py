"""
Generate "overlap clusters" from elements that are clustered the same
in a set of given clusterings

"""
import sys
import argparse

def read_clusterings(filenames):
    cluster_map = {}
    num_clusterings = len(filenames)
    for cl_num, fname in enumerate(filenames):
        # filepath = cluster_dir + fname
        filepath = fname
        try: fp = open(filepath, 'r')
        except IOError:
            sys.exit('Could not open file: {}'.format(filepath))
        for l_num, l in enumerate(fp.readlines()):
            for node in l.rstrip().split()[2:]:
                if node not in cluster_map:
                    cluster_map[node] = [-1] * num_clusterings
                cluster_map[node][cl_num] = l_num
        fp.close()
    return cluster_map

def get_cluster_filenames(index_file):
    try:
        fp = open(index_file, 'r')
    except IOError:
        sys.exit('Could not open file: {}'.format(index_file))
    filenames = [line.rstrip() for line in fp.readlines()]
    fp.close()
    return filenames

def generate_coassociation_map(cluster_map, node_list):
    co_map = {}
    for node, v in cluster_map.iteritems():
        if -1 in v or '-1' in v:
            continue
        cl_key = tuple(v)
        if cl_key not in co_map:
            co_map[cl_key] = [node]
        else:
            co_map[cl_key].append(node)
    return co_map

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("cluster_file",
                        help="File containing cluster filepaths")
    opts = parser.parse_args()
    filenames = get_cluster_filenames(opts.cluster_file)
    cluster_map = read_clusterings(filenames)
    node_list = cluster_map.keys()
    co_map = generate_coassociation_map(cluster_map, node_list)

    for idx, (key, cluster) in enumerate(co_map.iteritems()):
        cluster_text = '\t'.join(str(i) for i in cluster)
        print '{}\t1.0\t{}'.format(idx+1, cluster_text)


if __name__ == '__main__':
    main()
