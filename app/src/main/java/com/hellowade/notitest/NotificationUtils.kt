//package com.hellowade.notitest
//
//import android.R
//import android.annotation.SuppressLint
//import android.annotation.TargetApi
//import android.app.Notification
//import android.content.Context
//import android.os.Build
//import android.os.Bundle
//import android.service.notification.NotificationListenerService
//import android.service.notification.NotificationListenerService.Ranking
//import android.service.notification.NotificationListenerService.RankingMap
//import android.service.notification.StatusBarNotification
//import android.text.TextUtils
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.RemoteViews
//import android.widget.TextView
//import androidx.annotation.RequiresApi
//import androidx.core.app.NotificationCompat
//import models.Action
//import models.NotificationIds
//import java.util.Locale
//import java.util.concurrent.TimeUnit
//
//object NotificationUtils {
//    private val REPLY_KEYWORDS = arrayOf("reply", "android.intent.extra.text")
//    private val REPLY_KEYWORD: CharSequence = "reply"
//    private val INPUT_KEYWORD: CharSequence = "input"
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//    fun isRecent(sbn: StatusBarNotification, recentTimeframeInSecs: Long): Boolean {
//        return sbn.notification.`when` > 0 &&  //Checks against real time to make sure its new
//                System.currentTimeMillis() - sbn.notification.`when` <= TimeUnit.SECONDS.toMillis(
//            recentTimeframeInSecs
//        )
//    }
//
//    /**
//     * http://stackoverflow.com/questions/9292032/extract-notification-text-from-parcelable-contentview-or-contentintent *
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    fun notificationMatchesFilter(sbn: StatusBarNotification, rankingMap: RankingMap): Boolean {
//        val ranking = Ranking()
//        if (rankingMap.getRanking(
//                sbn.key,
//                ranking
//            )
//        ) if (ranking.matchesInterruptionFilter()) return true
//        return false
//    }
//
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    fun getMessage(extras: Bundle): String? {
//        Log.d("NOTIFICATIONUTILS", "Getting message from extras..")
//        Log.d("Text", "" + extras.getCharSequence(Notification.EXTRA_TEXT))
//        Log.d("Big Text", "" + extras.getCharSequence(Notification.EXTRA_BIG_TEXT))
//        Log.d("Title Big", "" + extras.getCharSequence(Notification.EXTRA_TITLE_BIG))
//        //        Log.d("Text lines", "" + extras.getCharSequence(Notification.EXTRA_TEXT_LINES));
//        Log.d("Info text", "" + extras.getCharSequence(Notification.EXTRA_INFO_TEXT))
//        Log.d("Info text", "" + extras.getCharSequence(Notification.EXTRA_INFO_TEXT))
//        Log.d("Subtext", "" + extras.getCharSequence(Notification.EXTRA_SUB_TEXT))
//        Log.d("Summary", "" + extras.getString(Notification.EXTRA_SUMMARY_TEXT))
//        var chars = extras.getCharSequence(Notification.EXTRA_TEXT)
//        return if (!TextUtils.isEmpty(chars)) chars.toString() else if (!TextUtils.isEmpty(extras.getString(
//                Notification.EXTRA_SUMMARY_TEXT
//            ).also {
//                chars = it
//            })
//        ) chars.toString() else null
//    }
//
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    fun getExtended(extras: Bundle, v: ViewGroup): String? {
//        Log.d("NOTIFICATIONUTILS", "Getting message from extras..")
//        val lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
//        if (lines != null && lines.size > 0) {
//            val sb = StringBuilder()
//            for (msg in lines)  //                msg = msg.toString();//.replaceAll("(\\s+$|^\\s+)", "").replaceAll("\n+", "\n");
//                if (!TextUtils.isEmpty(msg)) {
//                    sb.append(msg.toString())
//                    sb.append('\n')
//                }
//            return sb.toString().trim { it <= ' ' }
//        }
//        val chars = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
//        return if (!TextUtils.isEmpty(chars)) chars.toString() else if (!VersionUtils.isJellyBeanMR2()) getExtended(
//            v
//        ) else getMessage(extras)
//    }
//
//    @SuppressLint("NewApi")
//    fun getMessageView(context: Context, n: Notification): ViewGroup? {
//        Log.d("NOTIFICATIONUTILS", "Getting message view..")
//        var views: RemoteViews? = null
//        if (Build.VERSION.SDK_INT >= 16) views = n.bigContentView
//        if (views == null) views = n.contentView
//        if (views == null) return null
//        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val localView = inflater.inflate(views.layoutId, null) as ViewGroup
//        views.reapply(context.applicationContext, localView)
//        return localView
//    }
//
//    fun getTitle(localView: ViewGroup): String? {
//        Log.d("NOTIFICATIONUTILS", "Getting title..")
//        var msg: String? = null
//        val context = localView.context
//        val tv =
//            localView.findViewById<View>(NotificationIds.getInstance(context).TITLE) as TextView
//        if (tv != null) msg = tv.text.toString()
//        return msg
//    }
//
//    fun getMessage(localView: ViewGroup): String? {
//        Log.d("NOTIFICATIONUTILS", "Getting message..")
//        var msg: String? = null
//        val context = localView.context
//        var tv =
//            localView.findViewById<View>(NotificationIds.getInstance(context).BIG_TEXT) as TextView
//        if (tv != null && !TextUtils.isEmpty(tv.text)) msg = tv.text.toString()
//        if (TextUtils.isEmpty(msg)) {
//            tv = localView.findViewById<View>(NotificationIds.getInstance(context).TEXT) as TextView
//            if (tv != null) msg = tv.text.toString()
//        }
//        return msg
//    }
//
//    fun getExtended(localView: ViewGroup): String {
//        Log.d("NOTIFICATIONUTILS", "Getting extended message..")
//        var msg: String? = ""
//        val context = localView.context
//        var tv =
//            localView.findViewById<View>(NotificationIds.getInstance(context).EMAIL_0) as TextView
//        if (tv != null && !TextUtils.isEmpty(tv.text)) msg += """
//     ${tv.text}
//
//     """.trimIndent()
//        tv = localView.findViewById<View>(NotificationIds.getInstance(context).EMAIL_1) as TextView
//        if (tv != null && !TextUtils.isEmpty(tv.text)) msg += """
//     ${tv.text}
//
//     """.trimIndent()
//        tv = localView.findViewById<View>(NotificationIds.getInstance(context).EMAIL_2) as TextView
//        if (tv != null && !TextUtils.isEmpty(tv.text)) msg += """
//     ${tv.text}
//
//     """.trimIndent()
//        tv = localView.findViewById<View>(NotificationIds.getInstance(context).EMAIL_3) as TextView
//        if (tv != null && !TextUtils.isEmpty(tv.text)) msg += """
//     ${tv.text}
//
//     """.trimIndent()
//        tv = localView.findViewById<View>(NotificationIds.getInstance(context).EMAIL_4) as TextView
//        if (tv != null && !TextUtils.isEmpty(tv.text)) msg += """
//     ${tv.text}
//
//     """.trimIndent()
//        tv = localView.findViewById<View>(NotificationIds.getInstance(context).EMAIL_5) as TextView
//        if (tv != null && !TextUtils.isEmpty(tv.text)) msg += """
//     ${tv.text}
//
//     """.trimIndent()
//        tv = localView.findViewById<View>(NotificationIds.getInstance(context).EMAIL_6) as TextView
//        if (tv != null && !TextUtils.isEmpty(tv.text)) msg += """
//     ${tv.text}
//
//     """.trimIndent()
//        //        tv = (TextView) localView.findViewById(NotificationIds.getInstance().INBOX_MORE);
////        if (tv != null && !TextUtils.isEmpty(tv.getText()))
////            msg += tv.getText().toString() + '\n';
//        if (msg!!.isEmpty()) msg = getExpandedText(localView)
//        if (msg.isEmpty()) msg = getMessage(localView)
//        return msg!!.trim { it <= ' ' }
//    }
//
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    fun getTitle(extras: Bundle): String? {
//        Log.d("NOTIFICATIONUTILS", "Getting title from extras..")
//        val msg = extras.getString(Notification.EXTRA_TITLE)
//        Log.d("Title Big", "" + extras.getString(Notification.EXTRA_TITLE_BIG))
//        return msg
//    }
//
//    /** OLD/CURRENT METHODS  */
//    fun getView(context: Context, view: RemoteViews): ViewGroup? {
//        var localView: ViewGroup? = null
//        try {
//            val inflater =
//                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            localView = inflater.inflate(view.layoutId, null) as ViewGroup
//            view.reapply(context, localView)
//        } catch (exp: Exception) {
//        }
//        return localView
//    }
//
//    @SuppressLint("NewApi")
//    fun getLocalView(context: Context, n: Notification): ViewGroup? {
//        var view: RemoteViews? = null
//        if (Build.VERSION.SDK_INT >= 16) {
//            view = n.bigContentView
//        }
//        if (view == null) {
//            view = n.contentView
//        }
//        var localView: ViewGroup? = null
//        try {
//            val inflater =
//                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            localView = inflater.inflate(view!!.layoutId, null) as ViewGroup
//            view.reapply(context, localView)
//        } catch (exp: Exception) {
//        }
//        return localView
//    }
//
//    fun getActions(
//        n: Notification?,
//        packageName: String?,
//        actions: ArrayList<Action?>
//    ): ArrayList<Action?> {
//        val wearableExtender = NotificationCompat.WearableExtender(
//            n!!
//        )
//        if (wearableExtender.actions.size > 0) {
//            for (action in wearableExtender.actions) actions.add(
//                Action(
//                    action, packageName, action.title.toString().lowercase(
//                        Locale.getDefault()
//                    ).contains(
//                        REPLY_KEYWORD
//                    )
//                )
//            )
//        }
//        return actions
//    }
//
//    fun getQuickReplyAction(n: Notification, packageName: String?): Action? {
//        var action: NotificationCompat.Action? = null
//        if (Build.VERSION.SDK_INT >= 24) action = getQuickReplyAction(n)
//        if (action == null) action = getWearReplyAction(n)
//        return if (action == null) null else Action(action, packageName, true)
//    }
//
//    private fun getQuickReplyAction(n: Notification): NotificationCompat.Action? {
//        for (i in 0 until NotificationCompat.getActionCount(n)) {
//            val action = NotificationCompat.getAction(n, i)
//            if (action!!.remoteInputs != null) {
//                for (x in action.remoteInputs!!.indices) {
//                    val remoteInput: RemoteInput = action.remoteInputs!![x]
//                    if (isKnownReplyKey(remoteInput.getResultKey())) return action
//                }
//            }
//        }
//        return null
//    }
//
//    private fun getWearReplyAction(n: Notification): NotificationCompat.Action? {
//        val wearableExtender = NotificationCompat.WearableExtender(n)
//        for (action in wearableExtender.actions) {
//            if (action.remoteInputs != null) {
//                for (x in action.remoteInputs!!.indices) {
//                    val remoteInput: RemoteInput = action.remoteInputs!![x]
//                    if (isKnownReplyKey(remoteInput.getResultKey())) return action else if (remoteInput.getResultKey()
//                            .toLowerCase().contains(
//                                INPUT_KEYWORD
//                            )
//                    ) return action
//                }
//            }
//        }
//        return null
//    }
//
//    private fun isKnownReplyKey(resultKey: String): Boolean {
//        var resultKey = resultKey
//        if (TextUtils.isEmpty(resultKey)) return false
//        resultKey = resultKey.lowercase(Locale.getDefault())
//        for (keyword in REPLY_KEYWORDS) if (resultKey.contains(keyword)) return true
//        return false
//    }
//
//    //OLD METHOD
//    fun getExpandedText(localView: ViewGroup?): String {
//        var text = ""
//        if (localView != null) {
//            val context = localView.context
//            var v: View?
//            // try to get big text
//            v =
//                localView.findViewById<View>(NotificationIds.getInstance(context).big_notification_content_text)
//            if (v != null && v is TextView) {
//                val s = v.text.toString()
//                if (s != "") {
//                    // add title string if available
//                    val titleView = localView.findViewById<View>(R.id.title)
//                    text = if (v != null && v is TextView) {
//                        val title = (titleView as TextView).text.toString()
//                        if (title != "") "$title $s" else s
//                    } else s
//                }
//            }
//
//            // try to extract details lines
//            v =
//                localView.findViewById<View>(NotificationIds.getInstance(context).inbox_notification_event_10_id)
//            if (v != null && v is TextView) {
//                val s = v.text
//                if (s != "") if (s != "") text += s.toString()
//            }
//            v =
//                localView.findViewById<View>(NotificationIds.getInstance(context).inbox_notification_event_9_id)
//            if (v != null && v is TextView) {
//                val s = v.text
//                if (s != "") text += """
//
//     $s
//     """.trimIndent()
//            }
//            v =
//                localView.findViewById<View>(NotificationIds.getInstance(context).inbox_notification_event_8_id)
//            if (v != null && v is TextView) {
//                val s = v.text
//                if (s != "") text += """
//
//     $s
//     """.trimIndent()
//            }
//            v =
//                localView.findViewById<View>(NotificationIds.getInstance(context).inbox_notification_event_7_id)
//            if (v != null && v is TextView) {
//                val s = v.text
//                if (s != "") text += """
//
//     $s
//     """.trimIndent()
//            }
//            v =
//                localView.findViewById<View>(NotificationIds.getInstance(context).inbox_notification_event_6_id)
//            if (v != null && v is TextView) {
//                val s = v.text
//                if (s != "") text += """
//
//     $s
//     """.trimIndent()
//            }
//            v =
//                localView.findViewById<View>(NotificationIds.getInstance(context).inbox_notification_event_5_id)
//            if (v != null && v is TextView) {
//                val s = v.text
//                if (s != "") text += """
//
//     $s
//     """.trimIndent()
//            }
//            v =
//                localView.findViewById<View>(NotificationIds.getInstance(context).inbox_notification_event_4_id)
//            if (v != null && v is TextView) {
//                val s = v.text
//                if (s != "") text += """
//
//     $s
//     """.trimIndent()
//            }
//            v =
//                localView.findViewById<View>(NotificationIds.getInstance(context).inbox_notification_event_3_id)
//            if (v != null && v is TextView) {
//                val s = v.text
//                if (s != "") text += """
//
//     $s
//     """.trimIndent()
//            }
//            v =
//                localView.findViewById<View>(NotificationIds.getInstance(context).inbox_notification_event_2_id)
//            if (v != null && v is TextView) {
//                val s = v.text
//                if (s != "") text += """
//
//     $s
//     """.trimIndent()
//            }
//            v =
//                localView.findViewById<View>(NotificationIds.getInstance(context).inbox_notification_event_1_id)
//            if (v != null && v is TextView) {
//                val s = v.text
//                if (s != "") text += """
//
//     $s
//     """.trimIndent()
//            }
//            if (text == "") //Last resort for Kik
//            {
//                // get title string if available
//                val titleView =
//                    localView.findViewById<View>(NotificationIds.getInstance(context).notification_title_id)
//                val bigTitleView =
//                    localView.findViewById<View>(NotificationIds.getInstance(context).big_notification_title_id)
//                val inboxTitleView =
//                    localView.findViewById<View>(NotificationIds.getInstance(context).inbox_notification_title_id)
//                if (titleView != null && titleView is TextView) {
//                    text += titleView.text.toString() + " - "
//                } else if (bigTitleView != null && bigTitleView is TextView) {
//                    text += (titleView as TextView).text
//                } else if (inboxTitleView != null && inboxTitleView is TextView) {
//                    text += (titleView as TextView).text
//                }
//                v =
//                    localView.findViewById<View>(NotificationIds.getInstance(context).notification_subtext_id)
//                if (v != null && v is TextView) {
//                    val s = v.text
//                    if (s != "") {
//                        text += s.toString()
//                    }
//                }
//            }
//        }
//        return text.trim { it <= ' ' }
//    }
//
//    fun isAPriorityMode(interruptionFilter: Int): Boolean {
//        return if (interruptionFilter == NotificationListenerService.INTERRUPTION_FILTER_NONE ||
//            interruptionFilter == NotificationListenerService.INTERRUPTION_FILTER_UNKNOWN
//        ) false else true
//    }
//}