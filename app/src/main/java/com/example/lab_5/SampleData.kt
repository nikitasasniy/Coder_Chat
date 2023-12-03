import com.example.lab_5.Message

/**
 * SampleData for Jetpack Compose Tutorial 
 */
object SampleData {
    // Sample conversation data
    val conversationSample = listOf(
        Message(
            "Lexi",
            "Test...Test...Test..."
        ),
        Message(
            "Lexi",
            """пока ждетё, хотите анекдот?""".trim()
        ),
        Message(
            "Lexi",
            """Реально ли прожить месяц, питаясь только хлебом и водой? 
                |Не вредно ли это для организма и не снижается ли 
                |работоспособность?""".trimMargin().trim()
        ),
        Message(
            "Юзаич322",
            "Прожить - реально, работоспособность - снижается. Но если надзиратель опытный, то" +
                    " работоспособность не снизится."
        ),
        Message(
            "Lexi",
            """я даже не знаю что тебе ещё рассказать, иди чай попей:)""".trim()
        ),
        Message(
            "Lexi",
            "всё ещё грузит?)"
        ),
        Message(
            "Lexi",
            "может тебе и ненадо сюда?"
        ),
        Message(
            "Lexi",
            "иди поспи, люблю тебя ^_^"
        ),
    )
}
