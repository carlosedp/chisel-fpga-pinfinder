# Main system clock (125 Mhz)
create_clock -name "io_clock" -period 8.000ns [get_ports {io_clock}]

# Automatically constrain PLL and other generated clocks
derive_pll_clocks -create_base_clocks

# Automatically calculate clock uncertainty to jitter and other effects.
derive_clock_uncertainty
