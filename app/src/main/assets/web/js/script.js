function loadSystemInfo() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {

            var info = JSON.parse(xhttp.responseText);
            document.getElementById("device_model").innerHTML = info.DeviceModel;
            document.getElementById("DeviceModelName").innerHTML = info.DeviceModel;

            document.getElementById("document_size").innerHTML = pharseFileSize(info.DocumentSize);
            document.getElementById("document_count").innerHTML = info.Documents;
            document.getElementById("spinner").hidden = true;
            //            alert(xhttp.responseText);
        } else {
                document.getElementById("spinner").hidden = false;
            // alert("Error");
        }


        function pharseFileSize(lengthBytes) {
            var size;
            if (lengthBytes <= 1024) {
                return lengthBytes.toFixed(2) + " bytes";
            }
            var m = lengthBytes;
            var cont = 0;

            while (m >= 1024) {
                cont++;
                m = m / 1024;
            }

            switch (cont) {
            case 1: // KB
                size = " KB";
                break;
            case 2: // MB
                size = " MB";
                break;
            case 3: // GB
                size = " GB";
                break;
            case 4: // TB
                size = " TB";
                break;
            default:
                size = " B";
            }
            return ((lengthBytes / (Math.pow(1024, cont)))).toFixed(2) + size;
        }
    };
    xhttp.open("GET", "/?Key=PhoneSystemInfo", true);
    xhttp.setRequestHeader("Content-type", "text/html");
    xhttp.send();
}

function loadDocs() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var docList = JSON.parse(xhttp.responseText);
            var innerDiv = "";
            for (i = 0; i < docList.length; i++) {
                                var file = docList[i];
                                var file_name, download;
                                var size, extension;
                               // if (file.fileType == 3) {
                                    size = pharseFileSize(file.size);
                                    extension = file.extension;
                                    file_name = '<td width="400px" style="padding-left: 20px;">' + file.showName + '</td>';
                                    download = '<td class="text-center" width="100px"><a href=\'' + file.path + '\' download><i class="fa fa-download fa-large"></i>Download</a></td>';
                               // } else {
                               //     size = "";
                               //     extension = "FOLDER"+ docList.length;
                               //     file_name = '<td width="400px" style="padding-left: 20px;"><a onclick="loadFiles(\'' + file.path + '\')">' + file.showName + '</a></td>';
                               //     download = '<td class="text-center" width="100px">-</td>';
                               // }

                                innerDiv += '<tr>' + file_name +
                                    '<td class="text-center" width="100px">' + extension + '</td>' +
                                    '<td class="text-center" width="100px">' + size + '</td>' +
                                    '<td class="text-center" width="250px">' + timeConverter(file.modifyTime) + '</td>' +
                                    download +
                                    '</tr>';

                            }
            document.getElementById("docs_list").innerHTML = innerDiv;
        } else {
            //alert("Error");
        }
    };

    xhttp.open("GET", "/?Key=DocList", true);
    xhttp.setRequestHeader("Content-type", "text/html");
    xhttp.send();
}

function timeConverter(UNIX_timestamp) {
    var d = new Date(parseInt(UNIX_timestamp));
    return d.getFullYear() + '-' + d.getMonth() + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes();
}

function pharseFileSize(lengthBytes) {
    var size;
    if (lengthBytes <= 1024) {
        return lengthBytes.toFixed(2) + " bytes";
    }
    var m = lengthBytes;
    var cont = 0;

    while (m >= 1024) {
        cont++;
        m = m / 1024;
    }

    switch (cont) {
    case 1: // KB
        size = " KB";
        break;
    case 2: // MB
        size = " MB";
        break;
    case 3: // GB
        size = " GB";
        break;
    case 4: // TB
        size = " TB";
        break;
    default:
        size = " B";
    }
    return ((lengthBytes / (Math.pow(1024, cont)))).toFixed(2) + size;
}
