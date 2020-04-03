var labelType, useGradients, nativeTextSupport, animate;

(function() {
  var ua = navigator.userAgent,
      iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
      typeOfCanvas = typeof HTMLCanvasElement,
      nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
      textSupport = nativeCanvasSupport 
        && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
  //I'm setting this based on the fact that ExCanvas provides text support for IE
  //and that as of today iPhone/iPad current text support is lame
  labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
  nativeTextSupport = labelType == 'Native';
  useGradients = nativeCanvasSupport;
  animate = !(iStuff || !nativeCanvasSupport);

})();

function init(edgeType){
  var jsonGraph = JSON.parse(json);
  // init data
  // end
  // init ForceDirected
  var fd = new $jit.ForceDirected({
    width: false,
    height: false,
    //id of the visualization container
    injectInto: 'infovisSchema',
    //Enable zooming and panning
    //by scrolling and DnD
    Navigation: {
      enable: true,
      //Enable panning events only if we're dragging the empty
      //canvas (and not a node).
      panning: 'avoid nodes',
      zooming: 10 //zoom speed. higher is more sensible
    },
    // Change node and edge styles such as
    // color and width.
    // These properties are also set per node
    // with dollar prefixed data-properties in the
    // JSON structure.
    Node: {
      overridable: true,
      color: '#333'
    },
    Edge: {
      overridable: true,
      color: '#333',
      lineWidth: 1,
      type: edgeType
    },
    //Native canvas text styling
    Label: {
      overridable: true,
      type: labelType, //Native or HTML
      size: 12,
      style: 'bold',
      color: '#333'
    },
    //Add Tips
    Tips: {
      enable: true,
      onShow: function() {}
    },
    // Add node events
    Events: {
      enable: true,
      type: 'Native',
      //Change cursor style when hovering a node
      onMouseEnter: function(node) {
        if(node.data.isResource){
            fd.canvas.getElement().style.cursor = 'pointer';
        }
      },
      onMouseLeave: function() {
        fd.canvas.getElement().style.cursor = '';
      },
      //Update node positions when dragged
      onDragMove: function(node, eventInfo, e) {
          var pos = eventInfo.getPos();
          node.pos.setc(pos.x, pos.y);
          fd.plot();
      },
      //Implement the same handler for touchscreens
      onTouchMove: function(node, eventInfo, e) {
        $jit.util.event.stop(e); //stop default touchmove event
        this.onDragMove(node, eventInfo, e);
      },
      //Add also a click handler to nodes
      onClick: function(node) {
        if(!node) return;
        console.log(node);
        if(node.data.isResource){
            window.open(node.data.link,'_blank');
        }
      }
    },
    //Number of iterations for the FD algorithm
    iterations: 200,
    //Edge length
    levelDistance: 130,
    // Add text to the labels. This method is only triggered
    // on label creation and only for DOM labels (not native canvas ones).
     onCreateLabel: function(domElement, node){
          domElement.innerHTML = node.name;
          var style = domElement.style;
          style.fontSize = "0.8em";
          style.color = "#ddd";
        },
        // Change node styles when DOM labels are placed
        // or moved.
        onPlaceLabel: function(domElement, node){
          var style = domElement.style;
          var left = parseInt(style.left);
          var top = parseInt(style.top);
          var w = domElement.offsetWidth;
          style.left = (left - w / 2) + 'px';
          style.top = (top + 10) + 'px';
          style.display = '';
      }
  });
  // load JSON data.
  fd.loadJSON(jsonGraph);
  // compute positions incrementally and animate.
  fd.computeIncremental({
    iter: 40,
    property: 'end',
    onStep: function(perc){},
    onComplete: function(){
      fd.animate({
        modes: ['linear'],
        transition: $jit.Trans.Quad.easeOut,
        duration: 2500
      });
    }
  });
}

 $jit.ForceDirected.Plot.EdgeTypes.implement({
   'labeled': {
     'render': function(adj, canvas) {
     this.edgeTypes.line.render.call(this, adj, canvas);
       var data = adj.data;
       if(data.labeltext) {
         var ctx = canvas.getCtx();
         var posFr = adj.nodeFrom.pos.getc(true);
         var posTo = adj.nodeTo.pos.getc(true);
         ctx.fillStyle = "#333";
         ctx.fillText(data.labeltext, (posFr.x + posTo.x)/2, (posFr.y + posTo.y)/2);
       }
     }
   }
 });

$(document).on('load',init('line'));