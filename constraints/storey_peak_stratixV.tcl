# Global
set_global_assignment -name RESERVE_ALL_UNUSED_PINS_WEAK_PULLUP "As output driving ground"

# Clock
set_location_assignment PIN_M23 -to io_clock
set_instance_assignment -name IO_STANDARD "SSTL-135" -to io_clock

# Reset

# LEDs
set_location_assignment PIN_A11 -to io_led0
set_instance_assignment -name IO_STANDARD "2.5 V" -to io_led0
set_instance_assignment -name CURRENT_STRENGTH_NEW 8MA -to io_led0
set_instance_assignment -name SLEW_RATE 0 -to io_led0

# Pins to be scanned
set_location_assignment PIN_A10 -to io_A10
set_instance_assignment -name IO_STANDARD "2.5 V" -to io_A10
set_instance_assignment -name SLEW_RATE 0 -to io_A10
set_instance_assignment -name CURRENT_STRENGTH_NEW 8MA -to io_A10

set_location_assignment PIN_B10 -to io_B10
set_instance_assignment -name IO_STANDARD "2.5 V" -to io_B10
set_instance_assignment -name SLEW_RATE 0 -to io_B10
set_instance_assignment -name CURRENT_STRENGTH_NEW 8MA -to io_B10
