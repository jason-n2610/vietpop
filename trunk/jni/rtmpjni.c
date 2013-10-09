#include <jni.h>
#include <android/log.h>
#include "rtmp.h"
#include "log.h"
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <time.h>
#include <sys/stat.h>
#include <dirent.h>
#include<fcntl.h>
#include<errno.h>
#include<stdio.h>

#define	MAX_BUFFER_SIZE 999999

char		buffer[MAX_BUFFER_SIZE];
int			bufferSize = 0;
RTMP*     	rtmp;
RTMPPacket	packet;
char*     	szhostname;
char*    	szurl;
char*   	szplaypath;
char*  		szapp;
int       	port;

int 		bGet;

pthread_mutex_t		countMutex = PTHREAD_MUTEX_INITIALIZER;
pthread_t			tServer, tConnect;

int    		threadID = 2008;
int       	thread_Connect_ID = 1902;
int       	bRunning = 0;
int      	bServerRunning = 0;

int serverSock, clientSock;

//---ham khoi tao server socket rieng
jint JNICALL Java_com_ppclink_vietpop_radio_RadioEngine_createServerSocket(JNIEnv* env, jobject obj) {
	serverSock = socket(AF_INET, SOCK_STREAM, 0);

	if(serverSock == -1) {
//		__android_log_print(ANDROID_LOG_DEBUG, "error_C_Socket_Creation", strerror (errno));
//		__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "loi tao serverSock");
		return -1;
	}

//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Tao serverSock thanh cong");

	struct sockaddr_in sin;
	//---khoi tao dia chi server---
	sin.sin_family = AF_INET;
	sin.sin_addr.s_addr = INADDR_ANY;
	sin.sin_port = htons(8888);	//---dung cong 8888 de thuc hien ket noi---

	//---bind server socket len giao dien mang---
	if(bind(serverSock, (struct sockaddr*)&sin, sizeof(sin)) == -1) {
//		__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Khong bind duoc server socket...!");
//		__android_log_print(ANDROID_LOG_DEBUG, "error_C_Socket_Creation", strerror(errno));
	  	return -1;
	}

	//---neu bind thanh cong---
//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Server dang doi ket noi o cong 8888...");
	listen(serverSock, 10);

	return 0;
}

//---tao server doi ket noi o localhost---
void* ServerThread(void* ptr) {
//	int serverSock, clientSock;
//	struct sockaddr_in sin;
//	errno = 0;
	//---tao serverSocket---
//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Khoi tao serverSock");

//	serverSock = socket(AF_INET, SOCK_STREAM, 0);

//	if(serverSock == -1) {
//		__android_log_print(ANDROID_LOG_DEBUG, "error_C_Socket_Creation", strerror (errno));
//		__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "loi tao serverSock");
//		return;
//	}

//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Tao serverSock thanh cong");

	//---khoi tao dia chi server---
//	sin.sin_family = AF_INET;
//	sin.sin_addr.s_addr = INADDR_ANY;
//	sin.sin_port = htons(8888);	//---dung cong 8888 de thuc hien ket noi---

	//---bind server socket len giao dien mang---
//	if(bind(serverSock, (struct sockaddr*)&sin, sizeof(sin)) == -1) {
//		__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Khong bind duoc server socket...!");
//		__android_log_print(ANDROID_LOG_DEBUG, "error_C_Socket_Creation", strerror(errno));
//	  	return;
//	}

	//---neu bind thanh cong---
//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Server dang doi ket noi o cong 8888...");
//	listen(serverSock, 10);

	//---tao clientSocket khi co ket noi toi---
	clientSock = accept(serverSock, 0, 0);
	if(clientSock < 0) {
//		__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Khong chap nhan ket noi...!");
		return;
	}

	//---neu ket noi thanh cong
//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Client da ket noi...!");
//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Bat dau streaming du lieu...");

	char 	message[99999];
	char*	requestLine;

	//---khoi tao message---
	memset((void*)message, (int)'\0', 99999 );

	//---so byte ma client ket noi gui toi---
	int sent = recv(clientSock, message, 99999, 0);

	//---hien thi message ma client ket noi gui toi---
//	__android_log_print(ANDROID_LOG_DEBUG, "Message tu client : ", message);
//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Log", message);

//	RTMP_Log(RTMP_LOGDEBUG, "%s", message);
	printf("%s", message);

	requestLine = strtok(message, "\t\n");

//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Log", requestLine);

	RTMP_Log(RTMP_LOGDEBUG, "%s", requestLine);

	sent = 0;	//---sent la so byte da gui cho phia client---

	//---phan header ma server se gui cho client ket noi toi---
	char*	mess = "HTTP/1.1 200 OK\r\nContent-Type: audio/mpeg\r\nServer: quybka-PC\r\n\r\n";

	//---server gui header nay cho client ket noi toi---
	send(clientSock, mess, strlen(mess), 0);

//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Header da duoc gui cho client...!");

	while(bServerRunning) {
		while(1) {
			pthread_mutex_lock(&countMutex);

			int size = bufferSize;

			pthread_mutex_unlock(&countMutex);

			if(size < 300) {
//				__android_log_print(ANDROID_LOG_DEBUG, "Server Send Status", "Server Send sleep...!");
				usleep(1/25);
			}
			else {
				break;
			}
		}

		//---send data---
		pthread_mutex_lock(&countMutex);

		sent += send(clientSock, buffer, 300, 0);	//---moi lan se gui 300 bytes cho phia client---

		memmove(buffer, buffer + 300, bufferSize - 300);	//---cap nhat lai buffer---

		bufferSize -= 300;

//		RTMP_Log(RTMP_LOGDEBUG, "Server da gui : %d", sent);
		printf("Server da gui : ", sent);

		pthread_mutex_unlock(&countMutex);
	}

	//---dong client socket---
	close(clientSock);
	int i = close(serverSock);

//	RTMP_Log(RTMP_LOGDEBUG, "Server socket da dong...: %d", i);

	bRunning = 0;

	pthread_exit(threadID);
}

//---phuong thuc ket noi rtmp server rieng
jint JNICALL Java_com_ppclink_vietpop_radio_RadioEngine_rtmpConnect(JNIEnv* env, jobject obj) {
	//---khai bao cac tham so ket noi---
	const char*	szflashver = "MAC 10,0,0,22,87";
	AVal hostname = {szhostname, strlen(szhostname)};
	AVal swfUrl = { 0, 0 };
	AVal tcUrl = { szurl, strlen(szurl) };
	AVal pageUrl = { 0, 0 };
	AVal app = { szapp, strlen(szapp) };
	AVal auth = { 0, 0 };
	AVal swfHash = { 0, 0 };
	uint32_t swfSize = 0;
	AVal flashVer = { szflashver, strlen(szflashver) };
	AVal sockshost = { 0, 0 };
	AVal playpath = {szplaypath,strlen(szplaypath)};
	AVal subscribepath = {0,0};

	//---khoi tao rtmp---
	rtmp = RTMP_Alloc();
	RTMP_Init(rtmp);

//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Khoi tao rtmp thanh cong...");

	//---thiet lap thong so de ket noi toi RTMP Server---
	RTMP_SetupStream(rtmp, RTMP_PROTOCOL_RTMP, &hostname, port, &sockshost, &playpath,
			   &tcUrl, &swfUrl, &pageUrl, &app, &auth, &swfHash, swfSize,
			   &flashVer, &subscribepath, 0, 0, 1, 5);

//	__android_log_print(ANDROID_LOG_DEBUG,"RTMP Library Status","Thiet lap thong so rtmp thanh cong...");

	//---thuc hien ket noi RTMP Server---
	if(RTMP_Connect(rtmp, 0)) {
//		__android_log_print(ANDROID_LOG_DEBUG,"RTMP Library Status","Ket noi RTMP server thanh cong...");
	}
	else
	{
//		__android_log_print(ANDROID_LOG_DEBUG,"RTMP Library Status","Ket noi RTMP khong thanh cong...");
		return -1;
	}

	if(!RTMP_ConnectStream(rtmp, 0)) {
		return -1;
	}
}

//---tao packet thread nhan va phan tich packet tu rtmp server---
void* packetThread(void* ptr) {
	//---khai bao cac tham so ket noi---
	const char*	szflashver = "MAC 10,0,0,22,87";
	AVal hostname = {szhostname, strlen(szhostname)};
	AVal swfUrl = { 0, 0 };
	AVal tcUrl = { szurl, strlen(szurl) };
	AVal pageUrl = { 0, 0 };
	AVal app = { szapp, strlen(szapp) };
	AVal auth = { 0, 0 };
	AVal swfHash = { 0, 0 };
	uint32_t swfSize = 0;
	AVal flashVer = { szflashver, strlen(szflashver) };
	AVal sockshost = { 0, 0 };
	AVal playpath = {szplaypath,strlen(szplaypath)};
	AVal subscribepath = {0,0};

	//---khoi tao rtmp---
	rtmp = RTMP_Alloc();
	RTMP_Init(rtmp);

//	__android_log_print(ANDROID_LOG_DEBUG, "RTMP Library Status", "Khoi tao rtmp thanh cong...");

	//---thiet lap thong so de ket noi toi RTMP Server---
	RTMP_SetupStream(rtmp, RTMP_PROTOCOL_RTMP, &hostname, port, &sockshost, &playpath,
			   &tcUrl, &swfUrl, &pageUrl, &app, &auth, &swfHash, swfSize,
			   &flashVer, &subscribepath, 0, 0, 1, 5);

//	__android_log_print(ANDROID_LOG_DEBUG,"RTMP Library Status","Thiet lap thong so rtmp thanh cong...");

	//---thuc hien ket noi RTMP Server---
	if(RTMP_Connect(rtmp, 0)) {
//		__android_log_print(ANDROID_LOG_DEBUG,"RTMP Library Status","Ket noi RTMP server thanh cong...");
	}
	else
	{
//		__android_log_print(ANDROID_LOG_DEBUG,"RTMP Library Status","Ket noi RTMP khong thanh cong...");
		return -1;
	}

	if(!RTMP_ConnectStream(rtmp, 0)) {
		return -1;
	}

	//---lay ve cac packet---
		while(bRunning) {
			bGet = RTMP_GetNextMediaPacket(rtmp, &packet);

			if(bGet != 1) {
//				__android_log_print(ANDROID_LOG_DEBUG,"RTMP Library Status","LOI GET NEXT MEDIA PACKET");
				break;
			}

			//---hien thi thong tin packet---
//			RTMP_Log(RTMP_LOGDEBUG," headerType: %d",packet.m_headerType);
//			RTMP_Log(RTMP_LOGDEBUG," packetType: %d",packet.m_packetType);
//			RTMP_Log(RTMP_LOGDEBUG," hasAbsTimestamp: %d",packet.m_hasAbsTimestamp);
//			RTMP_Log(RTMP_LOGDEBUG," nChannel: %d",packet.m_nChannel);
//			RTMP_Log(RTMP_LOGDEBUG," TimeStamp: %d",packet.m_nTimeStamp);
//			RTMP_Log(RTMP_LOGDEBUG," InfoField2: %d",packet.m_nInfoField2);
//			RTMP_Log(RTMP_LOGDEBUG," BodySize: %d",packet.m_nBodySize);
//			RTMP_Log(RTMP_LOGDEBUG," BytesRead: %d",packet.m_nBytesRead);
//			RTMP_Log(RTMP_LOGDEBUG," body: %s", packet.m_body);

			printf(" headerType: %d",packet.m_headerType);
			printf(" packetType: %d",packet.m_packetType);
			printf(" hasAbsTimestamp: %d",packet.m_hasAbsTimestamp);
			printf(" nChannel: %d",packet.m_nChannel);
			printf(" TimeStamp: %d",packet.m_nTimeStamp);
			printf(" InfoField2: %d",packet.m_nInfoField2);
			printf(" BodySize: %d",packet.m_nBodySize);
			printf(" BytesRead: %d",packet.m_nBytesRead);
			printf(" body: %s", packet.m_body);
			//---***---

			while(1) {
				pthread_mutex_lock(&countMutex);

				int size = bufferSize;

				pthread_mutex_unlock(&countMutex);

				//---neu kich thuoc buffer vuot qua MAX_BUFFER_SIZE thi cho thread sleep---
				if(size + packet.m_nBodySize > MAX_BUFFER_SIZE) {
//					__android_log_print(ANDROID_LOG_DEBUG, "Packet Thread Status", "Packet Thread dang o trang thai sleep");

					usleep(1/25);
				}
				else {
					break;
				}
			}

			//---kieu message la 22 : gom cac submessage ben trong---
			if(packet.m_packetType == 22 && packet.m_nBodySize == 4330) {
				pthread_mutex_lock(&countMutex);
				int a;
				for(a = 0; a < 10; a++) {
					memcpy(buffer + bufferSize, packet.m_body + 12 + (433 * a), 417);	//---cho them 417 bytes vao buffer---
					bufferSize += 417;
				}

//				RTMP_Log(RTMP_LOGDEBUG, "Kich thuoc buffer : %d", bufferSize);

				pthread_mutex_unlock(&countMutex);
			}

			//---truong hop kieu message la 0x16 va kich thuoc packet la 4606 bytes---
			if(packet.m_packetType == 22 && packet.m_nBodySize == 4606) {
				pthread_mutex_lock(&countMutex);

				int y;
				for(y = 0; y < 14; y++)
				{
					memcpy(buffer + bufferSize, packet.m_body + 12 + (329 * y), 313);
					bufferSize += 313;
				}

				pthread_mutex_unlock(&countMutex);
			}

			//---truong hop kieu message la 0x16 va kich thuoc packet la 3440 bytes---
			if(packet.m_packetType == 22 && (packet.m_nBodySize == 3440 || packet.m_nBodySize == 3612)) {
				pthread_mutex_lock(&countMutex);
				int z;
				for(z = 0; z < 20; z++)
				{
					memcpy(buffer + bufferSize, packet.m_body + 12 + (172 * z), 156);
					bufferSize += 156;
				}
				pthread_mutex_unlock(&countMutex);
			}

			//---neu kieu message la 0x08, kich thuoc phan header la 1 byte---
			if(packet.m_packetType == 8 && packet.m_nBodySize == 418) {
				pthread_mutex_lock(&countMutex);

				memcpy(buffer + bufferSize, packet.m_body + 1,packet.m_nBodySize - 1);
				bufferSize += (packet.m_nBodySize - 1);

				pthread_mutex_unlock(&countMutex);
			}
		}

	RTMP_Close(rtmp);
//	RTMP_Log(RTMP_LOGDEBUG, "Ket thuc PacketThread...!");

	memset((void*) buffer, (int)'\0', MAX_BUFFER_SIZE);
	pthread_exit(thread_Connect_ID);
}

//---khoi dong server, doi o cong 8888---
jint JNICALL Java_com_ppclink_vietpop_radio_RadioEngine_startServer (JNIEnv * env, jobject obj)
{
//	__android_log_print(ANDROID_LOG_DEBUG, "Server status ", "Server da khoi dong");

	bServerRunning = 1;

	pthread_create(&tServer, 0, ServerThread, threadID);
}

//---phuong thuc setup thong so rtmp connect
jint JNICALL Java_com_ppclink_vietpop_radio_RadioEngine_setupParams(JNIEnv* env, jobject this, jstring hostNameChannel, jstring urlChannel,
		jstring playPathChannel, jstring appChannel, jint jport) {

	szhostname = (*env) -> GetStringUTFChars(env, hostNameChannel, 0);
	szurl = (*env) -> GetStringUTFChars(env, urlChannel, 0);
	szplaypath = (*env) -> GetStringUTFChars(env, playPathChannel, 0);
	szapp = (*env) -> GetStringUTFChars(env, appChannel, 0);
	port = jport;

}

//---ham thuc hien ket noi toi RTMPServer, lay ve phan data vao buffer---
jint JNICALL Java_com_ppclink_vietpop_radio_RadioEngine_connect(JNIEnv* env, jobject this) {

	thread_Connect_ID++;

	bRunning = 1;

	pthread_create(&tConnect, 0, packetThread, thread_Connect_ID);
}

//---ham lay ve kich thuoc cua buffer hien tai---
jint JNICALL Java_com_ppclink_vietpop_radio_RadioEngine_getBufferSize(JNIEnv* env, jobject obj) {
	return bufferSize;
}

void Java_com_ppclink_vietpop_radio_RadioEngine_stop(JNIEnv* env, jobject obj) {
	pthread_mutex_lock(&countMutex);
	bufferSize = 0;
	pthread_mutex_unlock(&countMutex);
	//---dung server thread
	bServerRunning = 0;

//	__android_log_print(ANDROID_LOG_DEBUG, "VietPop ", "Goi ham stop() thanh cong");
}

void Java_com_ppclink_vietpop_radio_RadioEngine_reset(JNIEnv* env, jobject obj) {
	//---xoa rong buffer
	memset((void*) buffer, (int)'\0', MAX_BUFFER_SIZE);
	pthread_mutex_lock(&countMutex);
	bufferSize = 0;
	pthread_mutex_unlock(&countMutex);
}

//---test nativeMethod - goi nguoc lai method callBack tu java
JNIEXPORT void JNICALL Java_com_ppclink_vietpop_radio_RadioEngine_nativeMethod(JNIEnv *env, jobject obj, jint depth) {
	jclass class = (*env) -> GetObjectClass(env, obj);	//---load class
	jmethodID mid = (*env) -> GetMethodID(env, class, "callBack", "(I)V");

	if(mid == 0) {
		return;
	}
	RTMP_Log(RTMP_LOGDEBUG, "In C, depth = %d, about to enter Java\n", depth);
	(*env) -> CallVoidMethod(env, obj, mid, depth);
	RTMP_Log(RTMP_LOGDEBUG, "In C, depth = %d, back from Java\n", depth);
}

jint JNICALL Java_com_ppclink_vietpop_radio_RadioEngine_getGet(JNIEnv* env, jobject obj) {
	return bGet;
}

jint JNICALL Java_com_ppclink_vietpop_radio_RadioEngine_closeServerSocket(JNIEnv* env, jobject obj) {
	int i = close(serverSock);

//	RTMP_Log(RTMP_LOGDEBUG, "Server socket da dong...: %d", i);
	printf("Server socket da dong... : %d", i);

	bRunning = 0;
	bServerRunning = 0;
}
