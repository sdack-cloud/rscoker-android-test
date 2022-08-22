package com.example.rsocket

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.ByteBufUtil
import io.rsocket.RSocket
import io.rsocket.core.RSocketConnector
import io.rsocket.frame.decoder.PayloadDecoder
import io.rsocket.metadata.TaggingMetadataCodec
import io.rsocket.transport.netty.client.WebsocketClientTransport
import io.rsocket.util.ByteBufPayload
import io.rsocket.util.DefaultPayload
import kotlinx.android.synthetic.main.activity_main.*
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.net.URI
import java.time.Duration
import java.util.*


class MainActivity2 : AppCompatActivity() {


//    var clientRSocket: RSocket? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connect.setOnClickListener {
            connect()
        }
        request.setOnClickListener { request() }
        stream.setOnClickListener { stream() }
        fire.setOnClickListener { fire() }
        dispose.setOnClickListener { dispose() }
    }
//    val routeMetadata = TaggingMetadataCodec.createTaggingContent(
//        ByteBufAllocator.DEFAULT, Collections.singletonList("toMany")
//    )
    @RequiresApi(Build.VERSION_CODES.O)
    fun connect() {
//        val transport = WebsocketClientTransport.create(URI.create("ws://192.168.1.139:7002"))
////        val clientRSocket = RSocketConnector.connectWith(transport).block()
//        if (clientRSocket == null || clientRSocket!!.isDisposed) {
//            Log.e("TAG", "创建连接")
//
//            clientRSocket = RSocketConnector.create()
//                .setupPayload(Mono.just("tese").map(DefaultPayload::create))
//                .keepAlive(Duration.ofMinutes(10), Duration.ofMinutes(10))
//                .payloadDecoder(PayloadDecoder.ZERO_COPY)
//                .reconnect(Retry.fixedDelay(Long.MAX_VALUE,Duration.ofSeconds(10)))
//                .connect(transport)
//                .doOnSuccess {
//                    Log.e("TAG", "Success")
//                }
//                .doOnError {
//                    Log.e("TAG", "Error")
//                }.doFinally {
//                    Log.e("TAG", "Finally")
//                }
//                .block()
//        }
    }

    fun dispose() {
        Log.e("TAG", "断开连接")
//        if (clientRSocket != null && !clientRSocket!!.isDisposed) {
//            clientRSocket?.dispose()
//        }
    }
    fun request() {
        Log.e("TAG", "REQUEST")
//        if (clientRSocket != null && !clientRSocket!!.isDisposed) {
//            clientRSocket!!.requestResponse(
//                ByteBufPayload.create(
//                    ByteBufUtil.writeUtf8(ByteBufAllocator.DEFAULT, "HelloWorld1"), routeMetadata))
//                .log()
//                .block()
//        }
    }

    fun stream() {
        Log.e("TAG", "STREAM")

    }

    fun fire() {
        Log.e("TAG", "FIRE")

    }

}