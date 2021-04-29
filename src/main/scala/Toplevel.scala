import chisel3._
import chisel3.util._

// Toplevel
class Toplevel(frequency: Int, baudRate: Int = 115200) extends Module {
  val io = IO(new Bundle {
    val led0 = Output(Bool())
    // Pins to scan (Test on ULX3S)
    val tx   = Output(UInt(1.W))
    val led1 = Output(UInt(1.W))
  })

  // Send message to pins
  // This is not efficient since each pin gets it's own TX
  // Pins to scan (Test on ULX3S)
  val tx   = new PinFind("TX ", io.tx, frequency, baudRate)
  val led1 = new PinFind("LED1 ", io.led1, frequency, baudRate)

  // Heartbeat led
  val led              = RegInit(0.U(1.W))
  val (_, counterWrap) = Counter(true.B, frequency / 2)
  when(counterWrap) {
    led := ~led
  }
  io.led0 := led
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
    new Toplevel(25000000), // For ULX3S
    // new Toplevel(125000000), // For StoreyPeak
    args
  )
}
