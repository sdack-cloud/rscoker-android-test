package com.example.rsocket

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Space
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.CompositeByteBuf
import io.rsocket.Payload
import io.rsocket.RSocket
import io.rsocket.SocketAcceptor
import io.rsocket.core.RSocketConnector
import io.rsocket.core.Resume
import io.rsocket.metadata.*
import io.rsocket.transport.netty.client.WebsocketClientTransport
import io.rsocket.util.ByteBufPayload
import io.rsocket.util.DefaultPayload
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import reactor.util.retry.RetryBackoffSpec
import java.net.URI
import java.time.Duration
import java.util.*


class MainActivity : AppCompatActivity() {


    var clientRSocket: RSocket? = null

    lateinit var md: ByteArray

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val compositeByteBuf = CompositeByteBuf(ByteBufAllocator.DEFAULT, false, 1);
        val routingMetadata = TaggingMetadataCodec.createRoutingMetadata(
            ByteBufAllocator.DEFAULT,
            listOf("toOne")
        )
        CompositeMetadataCodec.encodeAndAddMetadata(
            compositeByteBuf,
            ByteBufAllocator.DEFAULT,
            WellKnownMimeType.MESSAGE_RSOCKET_ROUTING,
            routingMetadata.content
        )
        md = ByteBufUtil.getBytes(compositeByteBuf)


        connect.setOnClickListener {
            connect()
        }
        request.setOnClickListener { request() }
        stream.setOnClickListener { stream() }
        fire.setOnClickListener { fire() }
        dispose.setOnClickListener { dispose() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun connect() {

        GlobalScope.launch {
            val transport = WebsocketClientTransport.create(URI.create("ws://192.168.1.139:7002"))
//            try {
                if (clientRSocket == null || !clientRSocket!!.isDisposed) {
                    Log.e("TAG", "连接中。。。" )
                    clientRSocket = RSocketConnector.create()
                        .metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.string)
                        .dataMimeType(WellKnownMimeType.APPLICATION_JSON.string)
                        .setupPayload(DefaultPayload.create("ttest"))
                        .acceptor(
                            SocketAcceptor.forRequestResponse { payload: Payload ->
                                val route: String? = decodeRoute(payload.sliceMetadata())
                                payload.release()
                                if ("message" == route) {
                                    val meta = MetaVo.Meta.parseFrom(payload.data)
                                    Log.e("MESSAGE", meta.toString())
                                    return@forRequestResponse Mono.empty()
                                }
                                Mono.error(IllegalArgumentException("Route $route not found"))
                            }
                        )
                        .keepAlive(Duration.ofSeconds(30), Duration.ofMinutes(30))
//                        .reconnect(Retry.fixedDelay(3, Duration.ofSeconds(5)).doBeforeRetry {
//                            // 设置重试次数
//                            Log.e("Retry", ""+it.totalRetries())
//                        })
                        .resume(Resume().retry(Retry.fixedDelay(4, Duration.ofSeconds(5))))
//                        .resume(Resume().retry(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1))
//                            .maxBackoff(Duration.ofSeconds(16))
//                            .jitter(1.0)))
                        .connect(transport)
                        .doOnSuccess {
                            Log.e("TAG", "Success")

                        }
                        .doOnCancel {
                            Log.e("TAG", "Cancel")
                        }
                        .doOnError {
                            Log.e("TAG", "Error")
                            it.printStackTrace()
                        }.doFinally {
                            Log.e("TAG", "Finally")
                        }
                        .block()

                }
//            } catch (e :Exception) {
//                Log.e("Exception", "" )
//                e.printStackTrace()
//            }

            if (clientRSocket != null) {
                clientRSocket!!
                    .onClose()
                    .doOnSuccess {
                        Log.e("CLOSE", "Success")
                    }
                    .doOnCancel {
                        Log.e("CLOSE", "Cancel")
                    }
                    .doOnError {
                        Log.e("CLOSE", "Error")
                        it.printStackTrace()
                    }
                    .doFinally {
                        Log.e("CLOSE", "Finally")
                    }
                    .subscribe()
            }
        }


    }

    fun dispose() {
        Log.e("TAG", "断开连接")
        if (clientRSocket != null ) {
            if (!clientRSocket!!.isDisposed) {
                clientRSocket?.dispose()
            }
            clientRSocket = null
        }
    }


    fun request() {
        Log.e("TAG", "REQUEST")

        if (clientRSocket != null && !clientRSocket!!.isDisposed) {
            val builder: MetaVo.Meta.Builder = MetaVo.Meta.newBuilder()
            builder.setRemark(
                "你好你好你好"
            ).setV(3.5)
                .setDescribe(
                    "再见再见再见"
                )
            val build: MetaVo.Meta = builder.build()
            val block =
                clientRSocket!!.requestResponse(DefaultPayload.create(build.toByteArray(), md))
                    .doOnError {
                        Log.e("REQUEST", "Error")
                    }
                    .doOnCancel {
                        Log.e("REQUEST", "Cancel")
                    }
                    .doFinally {
                        Log.e("REQUEST", "Finally ")
                    }
                    .block()

        }
    }

    fun stream() {
        Log.e("TAG", "STREAM")
        Log.e("TAG",""+ clientRSocket)

    }

    fun fire() {
        Log.e("TAG", "FIRE")
        if (clientRSocket != null) {
            Log.e("availability", ""+clientRSocket!!.availability() )
        }


    }

    fun decodeRoute(metadata: ByteBuf?): String? {
        val compositeMetadata = CompositeMetadata(metadata, false)
        for (metadatum in compositeMetadata) {
            if (Objects.requireNonNull(metadatum.mimeType)
                    .equals(WellKnownMimeType.MESSAGE_RSOCKET_ROUTING.string)
            ) {
                return RoutingMetadata(metadatum.content).iterator().next()
            }
        }
        return null
    }
}


