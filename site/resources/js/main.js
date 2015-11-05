Rot17 = {
    map: null,

    write: function(w) {
        document.write(Rot17.convert(w));
    },
    
    convert: function(w) {
        Rot17.init();

        var s = "";
        for (i=0; i < w.length; i++) {
            var b = w.charAt(i);
            s += ((b>='A' && b<='Z') || (b>='a' && b<='z') ? Rot17.map[b] : b);
        }
        return s;
    },

    init: function() {
        if (Rot17.map != null)
            return;
              
        var map = new Array();
        var s   = "abcdefghijklmnopqrstuvwxyz";

        for (i=0; i<s.length; i++)
            map[s.charAt(i)] = s.charAt((i+9)%26);
        for (i=0; i<s.length; i++)
            map[s.charAt(i).toUpperCase()] = s.charAt((i+9)%26).toUpperCase();

        Rot17.map = map;
    }

    
};
