
SBT = sbt

# Generate Verilog code
hdl:
	$(SBT) run

clean:
	git clean -fd

