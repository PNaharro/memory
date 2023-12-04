import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
import 'package:web_socket_channel/io.dart';

// Access appData globaly with:
// AppData appData = Provider.of<AppData>(context);
// AppData appData = Provider.of<AppData>(context, listen: false)

enum ConnectionStatus {
  disconnected,
  disconnecting,
  connecting,
  connected,
}

class AppData with ChangeNotifier {
  String ip = "localhost";
  String port = "8000";
  String name = "";
  bool gameCompleted = false;
  String status = "";

  IOWebSocketChannel? _socketClient;
  ConnectionStatus connectionStatus = ConnectionStatus.disconnected;

  bool turno = false;
  String? mySocketId;
  List<String> clients = [];
  String selectedClient = "";
  int? selectedClientIndex;
  String messages = "";

  int cardIndex1 = 16, cardIndex2 = 16;
  int post1 = 0, post2 = 0;
  bool file_saving = false;
  bool file_loading = false;
  final Color hiddenCard = Colors.red;
  List<Color>? gameColors;
  List<String>? gameImg;

  final String hiddenCardpath = "assets/images/hidden.png";
  List<String> cards_list = [
    "assets/images/circle.png",
    "assets/images/triangle.png",
    "assets/images/circle.png",
    "assets/images/heart.png",
    "assets/images/star.png",
    "assets/images/triangle.png",
    "assets/images/star.png",
    "assets/images/heartP.png",
    "assets/images/circleR.png",
    "assets/images/triangleN.png",
    "assets/images/circleR.png",
    "assets/images/heart.png",
    "assets/images/starM.png",
    "assets/images/triangleN.png",
    "assets/images/starM.png",
    "assets/images/heartP.png",
  ];
  int totalCards = 0;
  final int cardCount = 16;
  List<Map<int, String>> matchCheck = [];
  List<Map<int, String>> matchCheckrival = [];
  void initGame() {
    print(mySocketId);
    gameColors = List.generate(cardCount, (index) => hiddenCard);
    gameImg = List.generate(cardCount, (index) => hiddenCardpath);
  }

  List<String> receiveImageList(List<String> imageNames) {
    // Agregar el prefijo a cada nombre de archivo
    List<String> imagePaths = imageNames.map((imageName) {
      return "assets/images/$imageName";
    }).toList();

    return imagePaths;
  }

  void voltearCarta(int cardIndex) {
    if (cardIndex >= 0 && cardIndex < gameImg!.length) {
      gameImg![cardIndex] = cards_list[cardIndex];
    }
  }

  void ocultarCarta(int cardIndex) {
    if (cardIndex >= 0 && cardIndex < gameImg!.length) {
      gameImg![cardIndex] = hiddenCardpath;
    }
  }

  AppData() {
    _getLocalIpAddress();
  }

  void _getLocalIpAddress() async {
    try {
      final List<NetworkInterface> interfaces = await NetworkInterface.list(
          type: InternetAddressType.IPv4, includeLoopback: false);
      if (interfaces.isNotEmpty) {
        final NetworkInterface interface = interfaces.first;
        final InternetAddress address = interface.addresses.first;
        ip = address.address;
        notifyListeners();
      }
    } catch (e) {
      // ignore: avoid_print
      print("Can't get local IP address : $e");
    }
  }

  void connectToServer() async {
    connectionStatus = ConnectionStatus.connecting;
    notifyListeners();

    // Simulate connection delay
    await Future.delayed(const Duration(seconds: 1));

    _socketClient = IOWebSocketChannel.connect("ws://$ip:$port");
    _socketClient!.stream.listen(
      (message) {
        final data = jsonDecode(message);

        if (connectionStatus != ConnectionStatus.connected) {
          connectionStatus = ConnectionStatus.connected;
        }

        switch (data['type']) {
          case 'table':
          case 'broadcast':
            print(data);
            if (data['value'] == "nombre") {
              nombrar();
            }
            if (data['from'] == "java") {
              String card1 = data['value1'];
              String card2 = data['value2'];
              int cardIndex1 = conversor(card1);
              voltearCarta(cardIndex1);
              int cardIndex2 = conversor(card2);
              voltearCarta(cardIndex2);
              matchCheckrival.add({cardIndex1: cards_list[cardIndex1]});
              matchCheckrival.add({cardIndex2: cards_list[cardIndex2]});
              print(matchCheckrival);
              if (matchCheckrival.length == 2) {
                if (matchCheckrival[0].values.first ==
                    matchCheckrival[1].values.first) {
                  print("true");
                  matchCheckrival.clear();
                } else {
                  print("false");
                  Future.delayed(Duration(milliseconds: 500), () {
                    ocultarCarta(cardIndex1);
                    ocultarCarta(cardIndex2);
                    matchCheckrival.clear();
                  });
                }
              }
              turno = true;
              status = "Tu turno" + name;
              break;
            }
          case 'end':
          case 'list':
            clients = (data['list'] as List).map((e) => e.toString()).toList();
            clients.remove(mySocketId);
            messages += "List of clients: ${data['list']}\n";
            break;
          case 'id':
            mySocketId = data['value'];
            messages += "Id received: ${data['value']}\n";
            break;
          case 'connected':
            clients.add(data['id']);
            clients.remove(mySocketId);
            messages += "Connected client: ${data['id']}\n";
            break;
          case 'disconnected':
            String removeId = data['id'];
            if (selectedClient == removeId) {
              selectedClient = "";
            }
            clients.remove(data['id']);
            messages += "Disconnected client: ${data['id']}\n";
            break;
          case 'private':
            messages +=
                "Private message from '${data['from']}': ${data['value']}\n";
            break;
          default:
            messages += "Message from '${data['from']}': ${data['value']}\n";
            break;
        }

        notifyListeners();
      },
      onError: (error) {
        connectionStatus = ConnectionStatus.disconnected;
        mySocketId = "";
        selectedClient = "";
        clients = [];
        messages = "";
        notifyListeners();
      },
      onDone: () {
        connectionStatus = ConnectionStatus.disconnected;
        mySocketId = "";
        selectedClient = "";
        clients = [];
        messages = "";
        notifyListeners();
      },
    );
  }

  int conversor(String btn) {
    int clickedButton = 16;
    switch (btn) {
      case "btn00":
        clickedButton = 0;
        break;
      case "btn01":
        clickedButton = 1;
        break;
      case "btn02":
        clickedButton = 2;
        break;
      case "btn03":
        clickedButton = 3;
        break;
      case "btn10":
        clickedButton = 4;
        break;
      case "btn11":
        clickedButton = 5;
        break;
      case "btn12":
        clickedButton = 6;
        break;
      case "btn13":
        clickedButton = 7;
        break;
      case "btn20":
        clickedButton = 8;
        break;
      case "btn21":
        clickedButton = 9;
        break;
      case "btn22":
        clickedButton = 10;
        break;
      case "btn23":
        clickedButton = 11;
        break;
      case "btn30":
        clickedButton = 12;
        break;
      case "btn31":
        clickedButton = 13;
        break;
      case "btn32":
        clickedButton = 14;
        break;
      case "btn33":
        clickedButton = 15;
        break;
    }
    return clickedButton;
  }

  disconnectFromServer() async {
    connectionStatus = ConnectionStatus.disconnecting;
    notifyListeners();

    // Simulate connection delay
    await Future.delayed(const Duration(seconds: 1));

    _socketClient!.sink.close();
  }

  selectClient(int index) {
    if (selectedClientIndex != index) {
      selectedClientIndex = index;
      selectedClient = clients[index];
    } else {
      selectedClientIndex = null;
      selectedClient = "";
    }
    notifyListeners();
  }

  refreshClientsList() {
    final message = {
      'type': 'list',
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  nombrar() {
    final message = {
      'type': 'name',
      'plat': 'flutter',
      'value': name,
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  send(int post1, int post2) {
    if (selectedClientIndex == null) {
      PositionedMensaje(post1, post2);
    }
  }

  broadcastMessage(String msg) {
    final message = {
      'type': 'broadcast',
      'value': msg,
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  PositionedMensaje(int post1, int post2) {
    final message = {
      'type': 'broadcast',
      'from': 'flutter',
      'name': name,
      'value1': post1.toString(),
      'value2': post2.toString(),
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  privateMessage(String msg) {
    if (selectedClient == "") return;
    final message = {
      'type': 'private',
      'value': msg,
      'destination': selectedClient,
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  sendt() {
    _socketClient!.sink.add(jsonEncode(cards_list));
  }

  list_to_string() {}

  /*
  * Save file example:

    final myData = {
      'type': 'list',
      'clients': clients,
      'selectedClient': selectedClient,
      // i més camps que vulguis guardar
    };
    
    await saveFile('myData.json', myData);

  */
  void adios() {
    if (countRevealedImages() == 16) {
      print("adios");
      disconnectFromServer();
    }
  }

  bool areImagesEqual(int index1, int index2) {
    if (index1 >= 0 &&
        index1 < gameImg!.length &&
        index2 >= 0 &&
        index2 < gameImg!.length) {
      return gameImg![index1] == gameImg![index2];
    }
    return false;
  }

  void handleCardTap(int index, AppData appData,
      Function(VoidCallback) setStateCallback, BuildContext context) {
    // Verificar si es el turno del jugador
    if (!appData.turno) {
      return; // No hacer nada si no es el turno del jugador
    }

    print(appData.matchCheck);
    setStateCallback(() {
      appData.gameImg![index] = appData.cards_list[index];
      appData.matchCheck.add({index: appData.cards_list[index]});
      print(appData.matchCheck.first);
    });

    if (appData.matchCheck.length == 2 &&
        !(appData.matchCheck[0].keys.first ==
            appData.matchCheck[1].keys.first)) {
      post1 = appData.matchCheck[0].keys.first;
      post2 = appData.matchCheck[1].keys.first;

      if (appData.selectedClientIndex == null) {
        appData.send(post1, post2);
      }

      if (appData.matchCheck[0].values.first ==
          appData.matchCheck[1].values.first) {
        print("true");
        appData.matchCheck.clear();
        turno = false; // Verificar si se completó el juego
      } else {
        print("false");
        Future.delayed(Duration(milliseconds: 400), () {
          setStateCallback(() {
            appData.gameImg![appData.matchCheck[0].keys.first] =
                appData.hiddenCardpath;
            appData.gameImg![appData.matchCheck[1].keys.first] =
                appData.hiddenCardpath;
            appData..matchCheck.clear();
            turno = false;
          });
        });
      }
    }
    if (appData.matchCheck.length == 2 &&
        (appData.matchCheck[0].keys.first ==
            appData.matchCheck[1].keys.first)) {
      appData.gameImg![appData.matchCheck[0].keys.first] =
          appData.hiddenCardpath;
      appData.gameImg![appData.matchCheck[1].keys.first] =
          appData.hiddenCardpath;
      appData.matchCheck.clear();
    }
  }

  int countRevealedImages() {
    return gameImg!.where((img) => img != hiddenCardpath).length;
  }

  Future<void> saveFile(String fileName, Map<String, dynamic> data) async {
    file_saving = true;
    notifyListeners();

    try {
      final directory = await getApplicationDocumentsDirectory();
      final file = File('${directory.path}/$fileName');
      final jsonData = jsonEncode(data);
      await file.writeAsString(jsonData);
    } catch (e) {
      // ignore: avoid_print
      print("Error saving file: $e");
    } finally {
      file_saving = false;
      notifyListeners();
    }
  }

  /*
  * Read file example:
  
    final data = await readFile('myData.json');

  */

  Future<Map<String, dynamic>?> readFile(String fileName) async {
    file_loading = true;
    notifyListeners();

    try {
      final directory = await getApplicationDocumentsDirectory();
      final file = File('${directory.path}/$fileName');
      if (await file.exists()) {
        final jsonData = await file.readAsString();
        final data = jsonDecode(jsonData) as Map<String, dynamic>;
        return data;
      } else {
        // ignore: avoid_print
        print("File does not exist!");
        return null;
      }
    } catch (e) {
      // ignore: avoid_print
      print("Error reading file: $e");
      return null;
    } finally {
      file_loading = false;
      notifyListeners();
    }
  }
}
