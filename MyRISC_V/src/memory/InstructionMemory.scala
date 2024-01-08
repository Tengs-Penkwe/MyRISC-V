package MyRISC_V

import chisel3._

/** Memory interface
 *
 *  We are 64-bits architecture, but we only use 40 bits for addressing.
 *
 *
 * Space after 4 GiB is for On-Chip RAM
 * 0x 1_0000_0000 - 0x FF_FFFF_FFFF: RAM
 *
 */
class Memory(bitWidth: Int = 32) extends Module {

}
