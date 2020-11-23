/*
 *
 * @type {Array}
 * Πίνακες με τις συντεταγμένες των τοποθεσίων
 */
var locations_lat = [];
var locations_lon = [];

var locations_latB = [];
var locations_lonB = [];

var pageLinks = [];
var numberOfPageLinks = 0;
/*
 Πίνακας με τις περιγραφές των τοποθεσιών
 */
var locations_descriptions = [];
var locations_descriptionsB = [];
/*
 Πίνακας με τις ονομασίες των τοποθεσιών
 */
var locationNames = [];
/*
 Οι μεταβλητές χρησιμοποιούνται για να κρατάν τον αριθμό των τοποθεσιών που εντοπίστηκαν
 */
var numberOfLocations = 0;
var numberOfLocations0 = 0;
var numberOfLocations1 = 0;
var cnt;
var zomm;
var popup;
/*
 Η συνάρτηση τρέχει όταν γίνεται κλικ στον  χάρτη
 Εμφανίζει ένα pop up μήνυμα με τις συντεταγμένες της θέσης στην οποία έγινε κλικ
 */
function onMapClick(e) {
    popup
        .setLatLng(e.latlng)
        .setContent("Συντεταγμένες: " + e.latlng.toString())
        .openOn(mymap);
}
/*
 Η συνάρτηση εντοπίζει ανάμεσα από τα σημεία των τοποθεσιών που εντοπίστηκαν,
 τις συντατγμένες του κέντρου τους ώστε το κέντρο του χάρτη να προσαρμοστεί εκέι.
 (βρίσκει τον μ.ο. των lat και lon)
 */
function getCenter() {
    var sumLat = 0.0;
    var sumLon = 0.0;
    /*alert("Get Center");*/
    var i = 0;
    var j = 0;
    for (i = 0; i < numberOfLocations0; i++) {
        sumLat += locations_lat[i];
        sumLon += locations_lon[i];

        /*console.log(i+"-"+sumLat+","+sumLon);*/
    }

    for (j = 0; j < numberOfLocations1; j++) {
        sumLat += locations_latB[j];
        sumLon += locations_lonB[j];
        /*console.log(j+"-"+sumLat+","+sumLon);*/
    }
    /*console.log(j+i+"-"+sumLat+","+sumLon);*/
    var result = [sumLat / (i + j), sumLon / (i + j)];
    console.log(result);
    return result;
}
/*
 Ανάλογα με το εύρος κατα lat και lon επιλέγει το καταλληλότερο zoom για τον χάρτη
 */
function getZoom() {
    var min_lat = locations_lat[0];
    var min_lon = locations_lon[0];
    var max_lat = locations_lat[0];
    var max_lon = locations_lon[0];
    //ευρεση των ακραίων συντεταγμένων
    for (var i = 0; i < numberOfLocations0; i++) {
        if (locations_lat[i] < min_lat) {
            min_lat = locations_lat[i];
        }
        if (locations_lon[i] < min_lon) {
            min_lon = locations_lon[i];
        }
        if (locations_lat[i] > max_lat) {
            max_lat = locations_lat[i];
        }
        if (locations_lon[i] < max_lon) {
            max_lon = locations_lon[i];
        }
    }
    for (var j = 0; j < numberOfLocations1; j++) {
        if (locations_latB[j] < min_lat) {
            min_lat = locations_latB[j];
        }
        if (locations_lonB[j] < min_lon) {
            min_lon = locations_lonB[j];
        }
        if (locations_latB[j] > max_lat) {
            max_lat = locations_latB[j];
        }
        if (locations_lonB[j] < max_lon) {
            max_lon = locations_lonB[j];
        }
    }
    //Υπολογισμούς του μέγιστου εύρους
    var dif = max_lon - min_lon;
    dif = (dif < max_lat - min_lat ? max_lat - min_lat : dif);
    //Επιλογή zoom ανάλογα με το εύρος
    if (dif < 3) {
        return 15;
    } else if (dif < 6) {
        return 10;
    } else if (dif < 9) {
        return 7;
    } else {
        return 4;
    }
}

/*
function httpGet(theUrl)
{
    if (window.XMLHttpRequest)
    {
        xmlhttp = new XMLHttpRequest();
    } else
    {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function ()
    {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
        {
            return xmlhttp.responseText;
        }
    }
    xmlhttp.open("GET", theUrl, false);
    xmlhttp.send();
}*/

/*
 Εντοπίζει τοποθεσίες από αρχείο json με τα αποτελέσματα και τις εμφανίζει στον χάρτη
 */
function getJsonLocations() {
    //Ανάκτηση της τρέχουσας τοποθεσίας
    var currentUrl = window.location.href;
    // για δοκιμή σε local host. Ανάλογα με το domain που θα τρέχει θα πρέπει να γίνουν οι προσαρμογές
    //currentUrl = currentUrl.replace("http://localhost/Geo/demo/geo.html", "https://demos.isl.ics.forth.gr/elas4rdf/file");
    //διαμόρφωση του url που αντιστοιχεί στην παράθεση των αποτελεσμάτων σε μορφή JSON
    //currentUrl = currentUrl.replace("results/geo", "file?");
    //currentUrl = currentUrl + "&type=jsonld";
    //console.log(currentUrl);
    /*ΓΙΑ ΔΟΚΙΜΕΣ
    currentUrl = "file.json";*/
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    currentUrl = "https://localhost:8081/elas4rdf/file?type=jsonld&query="+urlParams.get('query')+"&size="+urlParams.get('size')
        //ανάκτηση των αποτελεσμάτων
        $.ajax({
            type: "GET",
            async: false,
            crossOrigin: true,
            context: {},
            url: currentUrl,
            success: function(data) {
                //ανάγνωση των αποτελεσμάτων
                var parsedJson = JSON.parse(data);
                var len = parsedJson['@graph'].length;
                for (var i = 0; i < len; i++) {
                    var obj = parsedJson['@graph'][i];
                    for (var key in obj) {
                        if (!obj.hasOwnProperty(key)) {
                            continue;
                        }
                        //εντοπισμός των τοποθεσιών (key = location / obj[key] = το όνομα της τοποθεσίας
                        //(το όνομα της τοποθεσίας είναι το τελευταίο συνθετικό
                        if (key == "location") {
                            var local = obj[key].split("/");
                            locationNames[numberOfLocations] = local[local.length - 1];
                            console.log(locationNames[numberOfLocations]);
                            numberOfLocations++;
                        }
                        //από κάθε τριπλέτα εντοπίζεται το πρώτο στοιχείο
                        //εντοπίζεται το url της και ελέγχεται αν είναι ...Place
                        //αν είναι τότε εντοπίζεται η θέση του
                        else if (key == "@id") {
                            //πρώτο στοιχέιο της τριπλέτας
                            pageLinks[numberOfPageLinks] = obj[key];
                            //αντικατάσταση του resource με page για να πάρουμε το url
                            //pageLinks[numberOfPageLinks] = pageLinks[numberOfPageLinks].replace("resource", "page");
                            console.log(pageLinks[numberOfPageLinks]);
                            $.ajax({
                                type: "GET",
                                crossOrigin: true,
                                async: false,
                                context: {},
                                url: pageLinks[numberOfPageLinks],
                                success: function(result) {
                                    var html = '<div>' + result + '</div>';
                                    //εδώ εντοπίζουμε αν πρόκειται για place
                                    var all = $(html).find(".page-resource-uri").find("a").attr('href');
                                    var temp = all;
                                    console.log(temp + " " + temp.includes("Place"));
                                    //εδώ εντοπίζουμε συντεταγμένες
                                    if (temp.includes("Place")) {
                                        var geoLat = $(html).find("span[property='geo:lat']").html();
                                        var geoLon = $(html).find("span[property='geo:long']").html();

                                        locations_latB[numberOfLocations1] = Number(geoLat);
                                        locations_lonB[numberOfLocations1] = Number(geoLon);
                                        locations_descriptionsB[numberOfLocations1] = pageLinks[numberOfPageLinks];
                                        numberOfLocations1++;
                                        alert("number of location find " + numberOfLocations1);
                                    }

                                }
                            });

                            numberOfPageLinks++;
                        }
                    };
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert(jqXHR.statusText + " " + textStatus + jqXHR.status + " " + textStatus + " " + errorThrown);
            }
        });
    //Από τα ονόματα των τοποθεσιών που ανακτήθηκαν, εντοπίζονται οι συντεταγμένες τους
    for (var i = 0; i < locationNames.length; i++) {
        var myurl = 'https://api.opencagedata.com/geocode/v1/json?q=' + locationNames[i] + '&key=31322e14e50c4319972b31c1aea14d15';
        console.log("-->" + locationNames[i]);
        $.ajax({
            url: myurl,
            crossDomain: true,
            async: false,
            dataType: "json",
            success: function(data) {
                //συντεταγμένες
                locations_lat[numberOfLocations0] = data.results[0].geometry.lat;
                locations_lon[numberOfLocations0] = data.results[0].geometry.lng;
                //ονομασία
                locations_descriptions[numberOfLocations0] = location[i];
                //πλήθος
                numberOfLocations0++;
            }
        });
    }
    //υπολογισμός του κέντρου του χάρτη
    cnt = getCenter();
    //υπολογισμός του κατάλληλου zoom
    zomm = getZoom();
    //δημιουργία του χάρτη
    var mymap = L.map('mapid').setView(cnt, zomm);
    L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
        maxZoom: 18,
        attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
            '<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
            'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
        id: 'mapbox/streets-v11',
        tileSize: 512,
        zoomOffset: -1
    }).addTo(mymap);
    //τοποθέτηση των τοποθεσιών στον χάρτη (κόκκινος κύκλος)
    for (var t = 0; t < numberOfLocations0; t++) {
        L.circle([locations_lat[t], locations_lon[t]], 5000, {
            color: 'red',
            fillColor: '#f03',
            fillOpacity: 0.5
        }).addTo(mymap).bindPopup(locations_descriptions[t]);
        popup = L.popup();
    }

    for (var t = 0; t < numberOfLocations1; t++) {
        L.circle([locations_latB[t], locations_lonB[t]], 5000, {
            color: 'red',
            fillColor: '#f03',
            fillOpacity: 0.5
        }).addTo(mymap).bindPopup(locations_descriptionsB[t]);
        popup = L.popup();
    }

    mymap.on('click', onMapClick);
}

/*
 * 1. Από το μέρος των αποτελεσμάτων που προβάλλονται στο tab triples (σύμφωνα με την κατανομή που κάνει το pagination)
 εντοπίζονται οι τοποθεσίες που φαίνονται σε αυτό. Ο εντοπισμός γίνεται με parsing του html κώδικα του triples tab
 * Συνάρτηση που ανακτά τις τοποθεσίες
 * που θα πρέπει να φανούν στον χάρτη

function getLocations() {
    var currentUrl = window.location.href;
    // για δοκιμές σε localhost currentUrl = currentUrl.replace("http://localhost:8081/","https://demos.isl.ics.forth.gr/");
    //alert(currentUrl);
    //εντοπισμός του tab triples
    currentUrl = currentUrl.replace("geo", "triples");
    //alert(currentUrl);
    //html parsing
    $.get(currentUrl, null, function (text) {
        var x = $(text).find('.big-col').find('.title').find('a');
        var y = $(text).find('.small-col').find('a');
        for (var i = 0; i < y.length; i++) {
            //εντοπίζονται τα location triples
            if (y.get(i) == "http://dbpedia.org/ontology/location") {
                var str = x.get(2 * i + 1) + "";
                var ar = str.split("/");
                //εντοπίζεται η ονομασία της τοποθεσίας
                var location = ar[ar.length - 1];
                //εντοπίζονται οι συντεταγμένες της τοποθεσίας
                var myurl = 'https://api.opencagedata.com/geocode/v1/json?q=' + location + '&key=31322e14e50c4319972b31c1aea14d15';
                $.ajax({
                    url: myurl,
                    crossDomain: true,
                    async: false,
                    dataType: "json",
                    success: function (data) {
                        locations_lat[numberOfLocations] = data.results[0].geometry.lat;
                        locations_lon[numberOfLocations] = data.results[0].geometry.lng;
                        locations_descriptions[numberOfLocations] = location;
                        numberOfLocations++;
                    }
                });
            }
        }
        //προετοιμασία του χάρτη και τοποθετηση των περιοχών
        cnt = getCenter();
        zomm = getZoom();
        var mymap = L.map('mapid').setView(cnt, zomm);
        L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
            maxZoom: 18,
            attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
                    '<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
                    'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
            id: 'mapbox/streets-v11',
            tileSize: 512,
            zoomOffset: -1
        }).addTo(mymap);
        for (var t = 0; t < numberOfLocations; t++) {
            L.circle([locations_lat[t], locations_lon[t]], 50, {
                color: 'red',
                fillColor: '#f03',
                fillOpacity: 0.5
            }).addTo(mymap).bindPopup(locations_descriptions[t]);
        }
        popup = L.popup();
        mymap.on('click', onMapClick);
    });
}
*/
$( document ).ready(function() {
    getJsonLocations();
});