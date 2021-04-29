# Define board parameters
SHELL = bash

# Project name and toplevel
project = toplevel
toplevel = Toplevel

scala_files = $(wildcard src/main/scala/*scala)
generated_files = generated
verilog_files = $(generated_files)/*.v

# Targets
chisel: $(verilog_files)   ## Generates Verilog code from Chisel sources using Mill
$(verilog_files): $(scala_files) clean
	scripts/mill $(project).run -td $(generated_files)

chisel-sbt:   ## Generates Verilog code from Chisel sources using SBT
	sbt "run -board ${BOARD} -td $(generated_files)"

chisel_tests:
	scripts/mill $(project).test

check: chisel_tests ## Run Chisel tests

fmt:
	scripts/mill all $(project).{reformat,fix}

clean: ## Clean all generated files
	@./scripts/mill clean
	@rm -rf obj_dir test_run_dir target
	@rm -rf $(generated_files)
	@rm -rf out
	@rm -f $(project)

.PHONY: chisel clean help

.DEFAULT_GOAL := chisel
