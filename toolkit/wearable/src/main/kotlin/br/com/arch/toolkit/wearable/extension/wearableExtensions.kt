package br.com.arch.toolkit.wearable.extension

import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val isMainThread: Boolean
    get() = Looper.myLooper() == Looper.getMainLooper()

private fun Context.getNearbyNodesByCapabilities(capability: String): Set<Node> = Tasks.await(
    Wearable.getCapabilityClient(this)
        .getCapability(capability, CapabilityClient.FILTER_REACHABLE)
).nodes

fun Context.registerCapability(capability: String) =
    Wearable.getCapabilityClient(this).addLocalCapability(capability)

fun Context.removeCapability(capability: String) =
    Wearable.getCapabilityClient(this).removeLocalCapability(capability)

fun Context.listenToWearableMessages(listener: MessageClient.OnMessageReceivedListener) =
    Wearable.getMessageClient(this).addListener(listener)

fun Context.stopListeningToWearableMessages(listener: MessageClient.OnMessageReceivedListener) =
    Wearable.getMessageClient(this).removeListener(listener)

fun Context.listenToWearableRequests(listener: MessageClient.RpcService, pathPrefix: String = "/") =
    Wearable.getMessageClient(this).addRpcService(listener, pathPrefix)

fun Context.stopListeningToWearableRequests(listener: MessageClient.RpcService) =
    Wearable.getMessageClient(this).removeRpcService(listener)

fun Context.sendDataToWearable(
    data: ByteArray,
    capability: String,
    onSuccessListener: (() -> Unit)? = null,
    onErrorListener: ((Throwable) -> Unit)? = null
) {
    if (isMainThread) {
        Log.e("WEARABLE", "Cannot communicate with wearable on the main thread. Aborting.")
        return
    }

    getNearbyNodesByCapabilities(capability).forEach { node ->
        Wearable.getMessageClient(this@sendDataToWearable)
            .sendMessage(node.id, capability, data)
            .addOnSuccessListener { onSuccessListener?.invoke() }
            .addOnFailureListener { onErrorListener?.invoke(it) }
    }
}

fun Context.requestDataFromWearable(
    request: ByteArray,
    capability: String,
    onSuccessListener: ((ByteArray) -> Unit)? = null,
    onErrorListener: ((Throwable) -> Unit)? = null
) {
    if (isMainThread) {
        Log.e("WEARABLE", "Cannot communicate with wearable on the main thread. Aborting.")
        return
    }

    getNearbyNodesByCapabilities(capability).forEach { node ->
        Wearable.getMessageClient(this)
            .sendRequest(node.id, capability, request)
            .addOnSuccessListener { onSuccessListener?.invoke(it) }
            .addOnFailureListener { onErrorListener?.invoke(it) }
    }
}

fun Context.sendDataToWearable(
    data: ByteArray,
    capability: String,
    coroutineScope: CoroutineScope,
    coroutineContext: CoroutineContext = Dispatchers.IO,
    onSuccessListener: (() -> Unit)? = null,
    onErrorListener: ((Throwable) -> Unit)? = null
) = coroutineScope.launch(coroutineContext) {
    sendDataToWearable(
        data,
        capability,
        { coroutineScope.launch(Dispatchers.Main) { onSuccessListener?.invoke() } },
        { coroutineScope.launch(Dispatchers.Main) { onErrorListener?.invoke(it) } }
    )
}

fun Context.requestDataFromWearable(
    request: ByteArray,
    capability: String,
    coroutineScope: CoroutineScope,
    coroutineContext: CoroutineContext = Dispatchers.IO,
    onSuccessListener: ((ByteArray) -> Unit)? = null,
    onErrorListener: ((Throwable) -> Unit)? = null
) = coroutineScope.launch(coroutineContext) {
    requestDataFromWearable(
        request,
        capability,
        { coroutineScope.launch(Dispatchers.Main) { onSuccessListener?.invoke(it) } },
        { coroutineScope.launch(Dispatchers.Main) { onErrorListener?.invoke(it) } }
    )
}

fun Context.replyRequest(
    nodeId: String,
    pathPrefix: String,
    data: ByteArray,
    onSuccessListener: ((ByteArray) -> Unit)? = null,
    onErrorListener: ((Throwable) -> Unit)? = null
) = Wearable.getMessageClient(this)
    .sendRequest(nodeId, pathPrefix, data)
    .addOnSuccessListener { onSuccessListener?.invoke(it) }
    .addOnFailureListener { onErrorListener?.invoke(it) }
