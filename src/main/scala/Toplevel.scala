import chisel3._
import chisel3.util._
import chisel3.experimental._
import firrtl.annotations.PresetAnnotation

// The Main object extending App to generate the Verilog code.
object Toplevel extends App {

  // Change the `pinsToTest` variable to test different pins.
  // Do not change the rest of the code.
  // Define pins to test. Must match the pin name from the constraints file for your board.
  val pinsToTest = Seq("A10", "B10", "AW26", "AV26")

  // Define the frequency of the clock signal in Hz for the board you are using.
  // For ULX3S, use 25000000
  // For StoreyPeak, use 125000000
  val frequency = 125000000

  // -----------------------------------------------------------------
  // Do not change the code below this line
  // -----------------------------------------------------------------

  // Generate Verilog
  (new chisel3.stage.ChiselStage).emitVerilog(
    new Toplevel(frequency), // For StoreyPeak
    args
  )
}

// Toplevel (use RawModule to define our own Clock and Reset)
class Toplevel(frequency: Int, pinsToTest: Seq[String], baudRate: Int = 115200) extends RawModule {
  val io = IO(new Bundle {
    val clock = Input(Clock())
    val led0  = Output(Bool())
    val pins = pinsToTest.map { pin =>
      pin -> Output(UInt(1.W))
    }.toMap
  })

  // Initialize registers to their reset value when the bitstream is programmed since there is no reset wire
  val reset = IO(Input(AsyncReset()))
  annotate(new ChiselAnnotation {
    override def toFirrtl = PresetAnnotation(reset.toTarget)
  })

  chisel3.withClockAndReset(io.clock, reset) {
    // Send message to pins
    pinsToTest.foreach { pin =>
      new PinFind(pin + " ", io.pins(pin), frequency, baudRate)
    }

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
