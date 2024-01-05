/*
 *
 * An ALU is a minimal start for a processor.
 *
 */

// package simple

import chisel3._
// import chisel3.util._
// import circt.stage.ChiselStage
import chisel3.util.{circt=>circtUtil, _}
import circt.stage.ChiselStage

/**
 * This is a very basic ALU example.
 */
class Hello extends Module {
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



// Generate the Verilog code
object HelloMain extends App {
  println("Generating the ALU hardware")
  // (new chisel.stage.CverilogString hiselStage).emitVerilog(new Hello(), Array("--target-dir", "generated"))
  emitVerilog(new Hello(), Array("--target-dir", "generated"))
  // chisel3.emitVerilog(new Hello())
  // val verilogString = chisel3.emitVerilog(new Hello())
  // println(verilogString)
}

