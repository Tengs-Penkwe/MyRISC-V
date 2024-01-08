package MyRISC_V

import chisel3._
import chisel3.util.{circt=>circtUtil, _}
import circt.stage.ChiselStage
import mainargs.{Leftover, ParserForMethods, arg, main}

/**
 * This is a very basic ALU example.
 */
class Toplevel (
    board: String, 
    invReset: Boolean = true, 
    cpufreq: Int,
) extends Module {

    val io = IO(new Bundle {
        val fn = Input(UInt(2.W))
        val a = Input(UInt(4.W))
        val b = Input(UInt(4.W))
        val result = Output(UInt(4.W))
    })

    // Use shorter variable names
    val fn = io.fn
    val a = io.a
    val b = io.b

    val result = Wire(UInt(4.W))
    // some default value is needed
    result := 0.U

    // The ALU selection
    switch(fn) {
        is(0.U) { result := a + b }
        is(1.U) { result := a - b }
        is(2.U) { result := a | b }
        is(3.U) { result := a & b }
    }

    // Output on the LEDs
    io.result := result
}

// The Main object extending App to generate the Verilog code.
object Toplevel {
    @main
    def run(
        // Parse command line arguments and extract required parameters
    @arg(short = 'b', doc = "FPGA Board to use") board:                 String = "bypass",
    @arg(short = 'r', doc = "FPGA Board have inverted reset") invreset: Boolean = false,
    @arg(short = 'f', doc = "CPU Frequency to run core") cpufreq:       Int = 50000000,
    @arg(short = 'c', doc = "Chisel arguments") chiselArgs:             Leftover[String],
    ) =
    // Generate SystemVerilog
    ChiselStage.emitSystemVerilogFile(
        new Toplevel(board, invreset, cpufreq),
        chiselArgs.value.toArray,
        Array(
            "--strip-debug-info",
            // Disables reg and memory randomization on initialization
            "--disable-all-randomization",
            // Creates memories with write masks in a single reg. Ref. https://github.com/llvm/circt/pull/4275
            "--lower-memories",
            // Avoids "unexpected TOK_AUTOMATIC" errors in Yosys. Ref. https://github.com/llvm/circt/issues/4751
            "--lowering-options=disallowLocalVariables,disallowPackedArrays",
        ),
    )

    println("Generated the Top hardware")

    def main(args: Array[String]): Unit =
        ParserForMethods(this).runOrExit(args.toIndexedSeq)
}

