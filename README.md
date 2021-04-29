# Chisel FPGA Pin Finder

The idea for this project came from RISC-V Brasil Telegram list (thanks [@racerxdl](https://github.com/racerxdl) and [@samsoniuk](https://github.com/samsoniuk)) for a wish to map the unidentified pins on the Storey Peak Stratix V FPGA board.

The idea is to add the pins to the IO and constraints file and instantiate the PinFinder class in that pin. This UART starts writing the pin name to the pin that can be captured by a USB-Serial converter and a console configured at 115200 baud, 8N1.

The project was tested on a Radiona ULX3S (where I already knew some pins) and then on the Storey Peak LED pins (that were already known too).

Next the idea is to map the other pins like PCIe, QSFP control pins and etc.

## Building

The first and recommended is using [Fusesoc](https://github.com/olofk/fusesoc), a package manager that handles all board backend files and configuration. It also makes adding support to new boards and vendors much easier.

### Fusesoc build and generation

To install Fusesoc (requires Python3 and pip3):

```sh
pip3 install --upgrade --user fusesoc

export PATH=~/.local/bin:$PATH
```

Check if it's working:

```sh
$ fusesoc --version
1.12.0
```

To generate the programming files for the **ULX3s**, first adjust the comments on `Toplevel.scala` file (pins on IO, pins to be scanned and the clock in the end of the file).

```sh
mkdir fusesoc-chiselblinky && cd fusesoc-chiselblinky

fusesoc library add fusesoc-cores https://github.com/fusesoc/fusesoc-cores
fusesoc library add pinfinder https://github.com/carlosedp/chisel-fpga-pinfinder

# Download the container command wrapper
wget https://gist.github.com/carlosedp/c0e29d55e48309a48961f2e3939acfe9/raw/bfeb1cfe2e188c1d5ced0b09aabc9902fdfda6aa/runme.py
chmod +x runme.py

# Run fusesoc with the wrapper as an environment var
EDALIZE_LAUNCHER=$(realpath ../runme.py) fusesoc run --target=ulx3s_85 carlosedp:demo:pinfinder

# Program to ULX3S
ujprog ./build/carlosedp_demo_pinfinder_0/ulx3s_85-trellis/carlosedp_demo_pinfinder_0.svf
```

For the Storey Peak board, run:

```sh
fusesoc run --target=storey_peak_stratixV --setup carlosedp:demo:pinfinder
```

This will generate the base files, `cd` into the generated directory (`./build/carlosedp_demo_pinfinder_0/storey_peak_stratixV-quartus`) in the machine Quartus is installed and run `make project`. Open the `carlosedp_demo_pinfinder_0.qpf` project for synthesize and PnR. Then program to the FPGA.

