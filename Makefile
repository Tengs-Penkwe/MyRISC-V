project   = MyRISC_V

SBT 			= sbt
MILL 			= mill

GEN_DIR 	= generated

SRCS 		 	= $(wildcard src/main/*/*.scala) 
GENS			=	$(wildcard $(GEN_DIR)/*.v)

LOG				=	iverilog
LOGFLAGS	=	-Wall 

VERILATOR	=	verilator
VERIFLAGS	=	
#-Wno-WIDTH -Wno-CASEINCOMPLETE -Wno-STMTDLY -Wno-UNUSED -Wno-INFINITELOOP -Wno-BLKSEQ
VERILATOR_ROOT	?=	$(shell bash -c 'verilator -V|grep VERILATOR_ROOT | head -1 | sed -e "s/^.*=\s*//"')
VINC 			:=	$(VERILATOR_ROOT)/include

# Set board PLL or bypass if not defined
BOARD ?= bypass
PLLFREQ ?= 50000000
BOARDPARAMS=--board ${BOARD} --cpufreq ${PLLFREQ}
# Check if generating for a different board/pll
$(if $(findstring $(shell cat .genboard 2>/dev/null),$(BOARDPARAMS)),,$(shell echo ${BOARDPARAMS} > .genboard))
# CHISELPARAMS = --target:fpga --emission-options=disableMemRandomization,disableRegisterRandomization
CHISELPARAMS = --split-verilog --target-dir $(GEN_DIR)

## Generates Verilog code from Chisel sources (output to ./generated)
chisel: $(GENS) build.sc Makefile .genboard 
	@rm -rf $@
	$(MILL) $(project).run $(BOARDPARAMS) $(CHISELPARAMS)
# @test "$(BOARD)" != "bypass" || (printf "Generating design with bypass PLL (for simulation). If required, set BOARD and PLLFREQ variables to one of the supported boards: " ; test -f chiselv.core && cat chiselv.core|grep "\-board"|cut -d '-' -f 3 | grep -v bypass | sed s/board\ //g |tr -s ' \n' ','| sed 's/,$$/\n/'; echo "Eg. make chisel BOARD=ulx3s PLLFREQ=15000000"; echo)


# Generate Verilog code
hdl:
	$(SBT) run

#===================================================================
# Icarus Verilog
#===================================================================

$(TARGET): $(GENS)
	@mkdir -p test/out test/waveform
	$(LOG) $(LOGFLAGS) $(LDFLAGS) -s $(TOP) -o $(addprefix test/out/,$(notdir $@)) $^ 

asm: $(ASMFILE)
	if [[ -z "${MIPS_CROSS}" ]]; then \
		export PATH="/opt/self/Cellar/mips-sde-elf/bin:$PATH"	\
		export MIPS_CROSS='True'	\
	fi
	make -C asm/

sim: $(TARGET) asm
	$(SIM) $(SIMFLAGS) $(addprefix test/out/,$(TARGET))

wave: sim
ifeq "$(OS)" "Darwin"
	$(shell /Applications/gtkwave.app/Contents/Resources/bin/gtkwave $(DumpFile) &)
else
	$(shell gtkwave $(DumpFile) &)
endif

i_clean:
	rm -rf  $(TARGET)

veri_clean:
	rm -rf obj_dir/ $(Target) $(Target).vcd 

git_clean:
	git clean -fd

clean: i_clean veri_clean git_clean
