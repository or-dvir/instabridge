# Android Assignment
The goal of this test is to check how much you know about object oriented principles, Android framework, 
design patterns and how you approach a problem and present its solution.

## Description
Write an android application that displays a list of wifis, when you click on any of the items it should show the details of the selected wifi.

![Mockup](https://github.com/Instabridge/android-assignment/blob/master/images/mock.png)

### List Screen
The main idea of the list screen is to display 2 groups of wifis: the **close by** group and the **further away** group.

* The items on the list should contain at least the SSID of the corresponding wifi, along with an icon representing its signal level.
* The **close by** group should display a maximum of 3 items.
* The **further away** group should also display a maximum of 3 items, along with a _SHOW MORE_ button.
* Whenever the _SHOW MORE_ button is clicked, append 3 more items (at most) to that group.

### Detail Screen
There are no specifications for this screen. Be creative and display the information you feel is most relevant.

### Information Source
We included a kotlin lib module that provides fake wifis, use `FakeWiFiProvider` to populate the list.

## **Nice to have**

Implement your own `WifiProvider`.
```
fun start()
fun stop()

interface Callback {
    fun onCloseByUpdate(items: List<WiFi>)
    fun onFarAwayUpdate(items: List<WiFi>)
}
```
- Provide the real content of the phone's native wifi manager in the method `onCloseByUpdate`.
- Provide data from an external source (e.g. read the information from a file ) in the method `onFarAwayUpdate`.

## Notes
- You shouldn't spend more than 8 hours on this assignment.
- Submit your solution with a brief description on how you tackled the problem and if you skipped some requirements or made any assumptions please mention them.

Have fun!

Instabridge's Android Team
