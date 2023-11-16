import java.net.NetworkInterface
import java.util.Collections
fun getMac(): String {
    val allNetworkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
    for (networkInterface in allNetworkInterfaces) {
        val macBytes = networkInterface.getHardwareAddress() ?: continue
        val res1 = StringBuilder()
        for (b in macBytes) {
            res1.append(String.format("%02X:",b))
        }
        if (res1.isNotEmpty()) {
            res1.deleteCharAt(res1.length - 1)
        }
        return res1.toString()
    }
    return "MAC Address not found"
}