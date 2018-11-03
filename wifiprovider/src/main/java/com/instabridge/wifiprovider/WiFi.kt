package com.instabridge.wifiprovider

import com.instabridge.wifiprovider.interfaces_and_enums.ESignalStrength
import java.io.Serializable
import java.util.*

data class WiFi(val ssid: String = "",
                var signalStrength: ESignalStrength = ESignalStrength.BEST,
                var distanceMeter: Int = 1000,
                var owner: String = "",
                var provider: String = "",
                var ip: String = "",
                var speedMbps: Int = 5,
                var security: String = "",
                var status: String = "")
    : Serializable
{
    init
    {
        val random = Random()

        //initializing variables with random values
        signalStrength = ESignalStrength.values()[random.nextInt(ESignalStrength.values().size)]
        distanceMeter = random.nextInt(1000)
        owner = getRandomOwner()
        provider = getRandomProvider()
        //need to use 256 because nextInt does not include the bound
        ip = "${random.nextInt(256)}.${random.nextInt(256)}.${random.nextInt(256)}.${random.nextInt(256)}"
        speedMbps = random.nextInt(1000)
        security = getRandomSecurityProtocol()
    }

    private fun getRandomSecurityProtocol(): String
    {
        val protocols = listOf("WEP", "WPA", "WPA2")
        return protocols[Random().nextInt(protocols.size)]
    }

    private fun getRandomProvider(): String
    {
        //names of swedish internet providers.
        //please don't disqualify me if im wrong :)
        //i got those names from wikipedia
        //https://en.wikipedia.org/wiki/Category:Internet_service_providers_of_Sweden

        val providers = listOf("Bahnhof",
                               "Bredbandsbolaget",
                               "Com Hem",
                               "Dataphone",
                               "Glocalnet",
                               "PRQ",
                               "Serious Tubes Networks",
                               "Tele2",
                               "Telenor",
                               "Telenordia",
                               "Telia Company")

        return providers[Random().nextInt(providers.size)]
    }

    private fun getRandomOwner(): String
    {
        //random names of places who have wifi...
        val owners = listOf("Mc'larens Pub",
                            "Dunder Mifflin Paper Company",
                            "Krusty Burger",
                            "Central Perk",
                            "The Winslow's")

        return owners[Random().nextInt(owners.size)]
    }
}