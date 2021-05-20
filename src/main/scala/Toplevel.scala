import chisel3._
import chisel3.util._
import chisel3.experimental._
import firrtl.annotations.PresetAnnotation

// Toplevel (use RawModule to define our own Clock and Reset)
class Toplevel(frequency: Int, baudRate: Int = 115200) extends RawModule {
  val io = IO(new Bundle {
    val clock = Input(Clock())
    val led0  = Output(Bool())
    //// Pins to scan (Test on ULX3S)
    // val tx   = Output(UInt(1.W))
    // val led1 = Output(UInt(1.W))
    //// Pins to scan (StoreyPeak)
    val A10  = Output(UInt(1.W))
    val B10  = Output(UInt(1.W))
    val AW26 = Output(UInt(1.W))
    val AV26 = Output(UInt(1.W))

  })

  // Initialize registers to their reset value when the bitstream is programmed since there is no reset wire
  val reset = IO(Input(AsyncReset()))
  annotate(new ChiselAnnotation {
    override def toFirrtl = PresetAnnotation(reset.toTarget)
  })

  chisel3.withClockAndReset(io.clock, reset) {
    // Send message to pins
    //// Pins to scan (Test on ULX3S)
    // new PinFind("TX ", io.tx, frequency, baudRate)
    // new PinFind("LED1 ", io.led1, frequency, baudRate)

    //// Pins to scan (StoreyPeak)
    new PinFind("A10 ", io.A10, frequency, baudRate)
    new PinFind("B10 ", io.B10, frequency, baudRate)
    new PinFind("AW26 ", io.AW26, frequency, baudRate)
    new PinFind("AV26 ", io.AV26, frequency, baudRate)

    // Heartbeat led
    val led              = RegInit(0.U(1.W))
    val (_, counterWrap) = Counter(true.B, frequency / 2)
    when(counterWrap) {
      led := ~led
    }
    io.led0 := led
  }
}

class PinFind(msg: String, output: UInt, frequency: Int, baudRate: Int) {
  val text = VecInit(msg.map(_.U))
  val len  = msg.length.U

  val tx = Module(new uart.BufferedTx(frequency, baudRate))
  output := tx.io.txd
  val cntReg = RegInit(0.U(8.W))
  tx.io.channel.bits := text(cntReg)
  tx.io.channel.valid := cntReg =/= len

  when(tx.io.channel.ready && cntReg =/= len) {
    cntReg := cntReg + 1.U
  }
  val (_, counterWrap) = Counter(true.B, frequency / 2)
  when(counterWrap) {
    cntReg := 0.U
  }
}

// The Main object extending App to generate the Verilog code.
object Toplevel extends App {
  // Generate Verilog
  (new chisel3.stage.ChiselStage).emitVerilog(
    // new Toplevel(25000000), // For ULX3S
    new Toplevel(125000000), // For StoreyPeak
    args
  )
}
