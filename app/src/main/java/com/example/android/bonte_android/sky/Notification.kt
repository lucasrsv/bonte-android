package com.example.android.bonte_android.sky

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.android.bonte_android.R

class Notification(context: Context) {
    private val CHANNEL_ID = "1000"
    private val mContext = context
    private val notificationTexts = arrayOf(
        Pair("celebre suas amizades \uD83E\uDD73", "que tal mostrar para algum amigo o quanto você é fã dele?"),
        Pair("o que você aprendeu hoje? \uD83E\uDD13", "compartilha com alguém aquela coisa nova que você descobriu!"),
        Pair("seja grato ☂️", "agradecer a alguém vai impactar o dia dessa pessoa, acredite!"),
        Pair("toma, é para você: \uD83C\uDF38", "que tal alegrar o dia de alguém repassando essa flor?"),
        Pair("ei, respira \uD83D\uDCA8", "preste atenção ao que você está sentindo. você merece uma pausa, ok?"),
        Pair("olhe ao seu redor \uD83D\uDCAB", "procure oportunidades para ajudar alguém!"),
        Pair("a empatia é um superpoder \uD83E\uDDB8\u200D♀️", "sendo assim, você já foi um herói hoje? ainda dá tempo!"),
        Pair("calce os sapatos de alguém hoje \uD83D\uDC5F", "ou seja, tente compreender a história e os sentimentos de alguém. \uD83D\uDE42"),
        Pair("seja gentil ✨", "com os outros e com você mesmo!"),
        Pair("“oi, tudo bom?” ☺️", "quando essa pergunta é sincera, ela muda o dia de alguém. ❤️"),
        Pair("pense em alguém que você admira \uD83D\uDE4C", "agora, diz para essa pessoa todos os motivos que te fazem admirá-la!"),
        Pair("você já olhou para o céu hoje? ☁️", "(sim, o céu de verdade! mas vem acender umas estrelas aqui também!)"),
        Pair("faça sua parte \uD83D\uDDD1", "que tal recolher um lixinho e jogar fora? isso deixa o mundo muito melhor!"),
        Pair("você está crescendo \uD83C\uDF31", "a cada dia, quando aprende algo novo, ou conversa com alguém diferente… ❤️"),
        Pair("elogie alguém hoje \uD83D\uDC9C", "e veja um sorriso sincero aparecer!"),
        Pair("comer é bom, né? \uD83C\uDF47", "que tal compartilhar um lanche com alguém hoje?"),
        Pair("você é uma pessoa incrível! \uD83E\uDD29","continue querendo melhorar a cada dia mais. ❤️"),
        Pair("sabe aquelas roupas que você não usa mais? \uD83D\uDC5A", "existe alguém por aí que seria muito grato por tê-las. que tal doá-las?"),
        Pair("você já passou por tanta coisa boa ⭐️", "tira um tempo para pensar nelas e compartilhar com alguém!"),
        Pair("abraços são poderosos \uD83D\uDC3B", "abrace alguém hoje e transmita esse poder!"),
        Pair("“ei, saudade de você” \uD83D\uDC9C", "e se você mandasse uma mensagem para aquela pessoa de quem você sente falta?"),
        Pair("continuamos lutando por um mundo mais gentil \uD83E\uDD8B", "e obrigada por fazer sua parte aqui no bontê! você é muito importante. ❤️"),
        Pair("cuide de si mesmo \uD83D\uDE0C", "não se esqueça de tirar um tempo para você, tá?"),
        Pair("que música incrível \uD83C\uDFA7", "manda sua música favorita de ultimamente para um amigo!"),
        Pair("a empatia é uma habilidade \uD83C\uDF1F", "e como toda habilidade, temos que praticar cada vez mais!"),
        Pair("brilha, brilha, estrelinha ✨", "a empatia é o que acende cada uma delas. ❤️"),
        Pair("lembre-se: cada um é cada um \uD83C\uDF19", "todos têm seus medos e amores. inclusive você. somos todos humanos."),
        Pair("compartilhe seu céu \uD83D\uDCAB", "orgulhe-se das suas estrelas acesas!\"")
    )

    fun createNotification() {
        val intent = Intent(mContext, SkyActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val notification = notificationTexts.random()
        val pendingIntent: PendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0)
        val builder: NotificationCompat.Builder? =
            mContext.let {
                NotificationCompat.Builder(it, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.bonte_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, R.drawable.star_on))
                    .setContentTitle(notification.first)
                    .setColor(Color.argb(255, 123, 91, 217))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(notification.second))
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Bontê notification channel"
            val descriptionText = "Bontê notification channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager? = mContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(channel)
        }

        if (builder != null) {
            with(mContext.let { NotificationManagerCompat.from(it) }) {
                this.notify(1, builder.build())
            }
        }
    }
}
