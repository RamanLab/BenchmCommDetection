# Community Detection Algorithms

This Python module contains the code for implementing 21 community detection algorithms. The module `community_detection_1` can be imported in an external script or accessed through the CLI.

## Requirements

- Java version 23.01
- R version >= 4.2
- Python version >= 3.10 (Python libraries required are mentioned in the `setup.py` file)
- WSL Environment

## Installation

1. Go to the directory containing the downloaded package in the terminal.
2. Run the following command:

    ```sh
    pip install -e .
    ```

## How to Run the Program

The Python module takes in an edgelist file or a NetworkX graph as input and returns a list of lists containing the communities. It optionally writes the output into a `.txt` file when the path for the same is provided. An example usage utilizing the Python CLI is highlighted below:

```sh
python -m community_detection_1.main network.dat MLRMCL --output_file output.txt--algorithm_args largest=50
 ```

`community_detection_1` is the name of the Python package. The input file is `network.dat` and the algorithm chosen for this example is `MLRMCL`. The output path is specified as `output.txt` and `largest=50` is one of the parameters of the algorithm.

## List of Implemented Algorithms and Corresponding Arguments

### General Algorithm Arguments
1. `weighted` (can be `True` or `False`)
2. `directed` (can be `True` or `False`)

### Specific Algorithms

1. **MLRMCL**
    - `largest` (integer indicating the maximum allowed size of a community)
    - `filters` ("quantile", "pageRank", "double")
    - `inteWeight` ("no", "yes")

2. **team_cs**
    - `Recursive` (True, False)

3. **tripleahc**
    - `t1` (weight threshold - lower limit)
    - `t2` (weight threshold - upper limit)

4. **zhenhua**
    - `max_limit` (integer indicating the maximum allowed size of a community)
    - `method` (1 for walktrap, 2 for infomap)

5. **walktrap**
    - `steps` (any integer)

6. **spin_glass**
    - `spins` (corresponds to number of communities)

7. **louvain**
    - `resolution` (0.1-10)

8. **fast_greedy**

9. **luminex**
    - `p` (0.1-10, corresponds to resolution)

10. **nextmr**
    - `min_limit` (minimum size of allowed clusters)

11. **Spectral_clustering**
    - `n_clusters` (number of output clusters)
    - `n_components` (dimension of latent representation)

12. **tuskdmi**
    - `num_com` (number of output communities)

13. **bigs2**
    - `min_size` (minimum size of output community)
    - `max_size` (maximum size of output community)
    - `Max_iter` (number of iterations)

14. **bluegenes**
    - `min_limit` (minimum size of output communities)
    - `alpha` (can be 1, 1.5, or 2)
    - `cut_size` (0.999 when community structure not very clear, 0.99 otherwise)

15. **SpecHier**
    - `groupNumber` (number of output communities)

16. **Dcut**
    - `min_limit` (minimum size of output community)
    - `max_limit` (maximum size of output community)

17. **csbioiitm_hamming**

18. **SVT**
    - `n_clusters` (number of output communities)

19. **label_propagation**
    - `spins` (upper limit for number of communities)

20. **shared_neighbor**
    - `limit` (any integer)

21. **girvan_newman**
    - `most_valuable_edge` (tuple (u,v))

## Example for Importing in an External Python Script
sh
```python
import community_detection_1.main as main
res = main.run_community_detection('network.dat', 'blue_genes')
```
