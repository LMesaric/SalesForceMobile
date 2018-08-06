/* License for implementation of SHA-3 in Kotlin (version 0.5)
 * https://github.com/walleth/sha3
 *
 * MIT License
 *
 * Copyright (c) 2017 ligi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package hr.atoscvc.salesforcemobile

import org.walleth.sha3.SHA3Parameter
import org.walleth.sha3.calculateSHA3

object HashSHA3 {
    fun getHashedValue(value: String): String {
        val hexChars = "0123456789abcdef"
        val valueHashed = value.calculateSHA3(SHA3Parameter.SHA3_512)
        val stringBuilder = StringBuilder(valueHashed.size * 2)

        for (i in 0 until valueHashed.size) {
            val byte = valueHashed[i]
            val left = byte.toInt().ushr(4).and(0b1111)
            val right = byte.toInt().and(0b1111)
            stringBuilder.append(hexChars[left]).append(hexChars[right])
        }

        return stringBuilder.toString()
    }
}