/*!
 * jQuery ClassyLoupe
 * www.class.pm
 *
 * Written by Marius Stanciu - Sergiu <marius@class.pm>
 * Licensed under the MIT license www.class.pm/LICENSE-MIT
 * Version 1.2.0
 *
 */

(function($) {
    var id = 0;
    jQuery.fn.ClassyLoupe = function(a) {
        id++;
        if (this.length > 1) {
            return this.each(function() {
                $(this).ClassyLoupe(a);
            }), this;
        }
        var a = $.extend({
            trigger: 'mouseenter',
            shape: 'circle',
            roundedCorners: 10,
            loupeToggleSpeed: 'medium',
            loupeToggleEasing: 'linear',
            size: 200,
            minSize: 150,
            maxSize: 250,
            glossy: true,
            shadow: true,
            resize: true,
            sizeSnap: 10,
            resizeAnimationSpeed: 'medium',
            resizeAnimationEasing: 'easeOutBack',
            canZoom: true,
            zoomKey: 90,
            zoom: 100,
            minZoom: 50,
            maxZoom: 200,
            zoomSnap: 5,
            zoomAnimationSpeed: 'medium',
            zoomAnimationEasing: 'easeOutBack',
            overlay: true,
            overlayOpacity: 0.5,
            overlayEffectSpeed: 'slow',
            overlayEffectEasing: 'easeOutBack',
            overlayClassName: ''
        }, a || {}), j = jQuery(this), c = 'classyloupe-' + id, t = 'classyloupe_overlay-' + id, h = a.size, i, q = null, u = 0, v = 0, x = 0, y = 0, r = 0, s = 0, w = false, p = false, k = a.zoom, n = 0, o = 0, e, z = false;
        return this.each(function() {
            function A() {
                var d = h - 2 * $('#' + c + ' .lglossy').css('marginTop'), e = h / 2, g = 0, f = 0;
                a.shape === 'circle' ? f = g = e : a.shape === 'rounded' && (g = parseInt($('#' + c).css('border-top-width')), f = g = a.roundedCorners - g);
                $('#' + c + ' .glossy').stop().animate({
                    width: d + 'px',
                    height: e + 'px',
                    '-webkit-border-top-left-radius': g + 'px',
                    '-webkit-border-top-right-radius': f + 'px',
                    '-moz-border-radius-topleft': g + 'px',
                    '-moz-border-radius-topright': f + 'px',
                    'border-top-left-radius': g + 'px',
                    'border-top-right-radius': f + 'px'
                }, {
                    queue: false,
                    easing: a.resizeAnimationEasing,
                    duration: a.resizeAnimationSpeed
                });
            }
            function B(d, e) {
                if (w && a.canZoom) {
                    if (!(k + a.zoomSnap * d > a.maxZoom || k + a.zoomSnap * d < a.minZoom)) {
                        k += a.zoomSnap * d;
                        r += Math.round(x * a.zoomSnap / 100) * d;
                        s += Math.round(y * a.zoomSnap / 100) * d;
                        var g = e.pageY - this.offsetTop;
                        n = Math.round(r / u * (e.pageX - this.offsetLeft)) * -1 + h / 2;
                        o = Math.round(s / v * g) * -1 + h / 2;
                        $('#' + c).animate({
                            'background-position': n + 'px ' + o + 'px',
                            'background-size': r + 'px ' + s + 'px'
                        }, {
                            queue: false,
                            easing: a.zoomAnimationEasing,
                            duration: a.zoomAnimationSpeed,
                            complete: function() {
                                i = $('#' + c).outerWidth();
                                var a = new jQuery.Event('mousemove', {
                                    pageX: m + i / 2,
                                    pageY: l + i / 2
                                });
                                j.trigger(a);
                            }
                        });
                    }
                }
                else if (a.resize && !w && (g = d * a.sizeSnap, !(h + g > a.maxSize || h + g < a.minSize))) {
                    h += g;
                    var f = 0, m = Math.round($('#' + c).offset().left - g), l = Math.round($('#' + c).offset().top - g);
                    n += g;
                    o += g;
                    $('#' + c).stop();
                    a.shape === 'circle' ? (f = h / 2, $('#' + c).animate({
                        width: h + 'px',
                        height: h + 'px',
                        '-webkit-border-top-left-radius': f + 'px',
                        '-webkit-border-top-right-radius': f + 'px',
                        '-webkit-border-bottom-left-radius': f + 'px',
                        '-webkit-border-bottom-right-radius': f + 'px',
                        '-moz-border-radius-topleft': f + 'px',
                        '-moz-border-radius-topright': f + 'px',
                        '-moz-border-radius-bottomleft': f + 'px',
                        '-moz-border-radius-bottomright': f + 'px',
                        'border-top-left-radius': f + 'px',
                        'border-top-right-radius': f + 'px',
                        'border-bottom-left-radius': f + 'px',
                        'border-bottom-right-radius': f + 'px',
                        'background-position': n + 'px ' + o + 'px',
                        left: m + 'px',
                        top: l + 'px'
                    }, {
                        queue: false,
                        easing: a.resizeAnimationEasing,
                        duration: a.resizeAnimationSpeed,
                        complete: function() {
                            i = $('#' + c).outerWidth();
                            var a = new jQuery.Event('mousemove', {
                                pageX: m + i / 2,
                                pageY: l + i / 2
                            });
                            j.trigger(a);
                        }
                    })) : a.shape === 'rounded' ? $('#' + c).animate({
                        width: h + 'px',
                        height: h + 'px',
                        '-webkit-border-radius': a.roundedCorners,
                        '-moz-border-radius': a.roundedCorners,
                        'border-radius': a.roundedCorners,
                        'background-position': n + 'px ' + o + 'px',
                        left: m + 'px',
                        top: l + 'px'
                    }, {
                        queue: false,
                        easing: a.resizeAnimationEasing,
                        duration: a.resizeAnimationSpeed,
                        complete: function() {
                            i = $('#' + c).outerWidth();
                            var a = new jQuery.Event('mousemove', {
                                pageX: m + i / 2,
                                pageY: l + i / 2
                            });
                            j.trigger(a);
                        }
                    }) : a.shape === 'square' && $('#' + c).animate({
                        width: h + 'px',
                        height: h + 'px',
                        'background-position': n + 'px ' + o + 'px',
                        left: m + 'px',
                        top: l + 'px'
                    }, {
                        queue: false,
                        easing: a.resizeAnimationEasing,
                        duration: a.resizeAnimationSpeed,
                        complete: function() {
                            i = $('#' + c).outerWidth();
                            var a = new jQuery.Event('mousemove', {
                                pageX: m + i / 2,
                                pageY: l + i / 2
                            });
                            j.trigger(a);
                        }
                    });
                    a.glossy && A();
                }
            }
            (function() {
                j.is("a") ? (q = j.attr('href'), e = j.find('img')) : (j.is('img') || j.is('input[type="image"]')) && (q = j.attr('src'), e = j);
                u = e.width();
                v = e.height();
                $('body').append('<div class="classyloupe" id="' + c + '"></div>');
                var d = new Image;
                d.onload = function() {
                    x = this.width;
                    y = this.height;
                    r = Math.round(x * k / 100);
                    s = Math.round(y * k / 100);
                    var d = h / 2;
                    $('#' + c).css({
                        width: h + 'px',
                        height: h + 'px',
                        'background-image': 'url(' + q + ')',
                        'background-size': r + 'px ' + s + 'px'
                    });
                    a.shape === 'circle' ? $('#' + c).css({
                        '-webkit-border-radius': d + 'px',
                        '-moz-border-radius': d + 'px',
                        'border-radius': d + 'px'
                    }) : a.shape === 'rounded' && $('#' + c).css({
                        '-webkit-border-radius': a.roundedCorners,
                        '-moz-border-radius': a.roundedCorners,
                        'border-radius': a.roundedCorners + 'px'
                    });
                    i = $('#' + c).outerWidth();
                    a.glossy && $('#' + c).append('<div class="lglossy"></div>');
                    a.overlay && ($('body').append("<div class='loverlay " + a.overlayClassName + "' id='" + t + "'></div>"), $('#' + t).css({
                        top: e.offset().top + 'px',
                        left: e.offset().left + 'px',
                        width: e.outerWidth() + 'px',
                        height: e.outerHeight() + 'px'
                    }));
                    a.shadow && $('#' + c).addClass('lshadow');
                };
                d.src = q;
            }(), (a.resize || a.canZoom) && !z && $.event.special.mousewheel && $('#' + c).bind('mousewheel', function(a, b) {
                B(b, a);
                return false;
            }), e.bind(a.trigger, function(d) {
                p ? ($('#' + c).fadeOut(a.loupeToggleSpeed, a.loupeToggleEasing), p = false, a.overlay && $('#' + t).fadeOut(a.overlayEffectSpeed, a.overlayEffectEasing)) : ($('#' + c).fadeIn(a.loupeToggleSpeed, a.loupeToggleEasing), p = true, a.overlay && $('#' + t).fadeTo(a.overlayEffectSpeed, a.overlayOpacity, a.overlayEffectEasing), A());
                if (d.type === 'click') {
                    return d.preventDefault ? d.preventDefault() : d.returnValue = false, false;
                }
            }), $('#' + c).bind('click', function() {
                e.trigger('click');
            }), $(document).bind('mousemove', function(d) {
                if (!p) {
                    return true;
                }
                var j = parseInt(e.css('border-left-width')) + parseInt(e.css('padding-left')),
                        g = parseInt(e.css('border-top-width')) + parseInt(e.css('padding-top')),
                        f = parseInt(e.css('border-right-width')) + parseInt(e.css('padding-right')),
                        m = parseInt(e.css('border-bottom-width')) + parseInt(e.css('padding-bottom')),
                        l = d.pageX - e.offset().left - j,
                        k = d.pageY - e.offset().top - g,
                        q = Math.round(d.pageX - i / 2),
                        d = Math.round(d.pageY - i / 2);
                n = Math.round(r / u * l) * -1 + h / 2;
                o = Math.round(s / v * k) * -1 + h / 2;
                $('#' + c).css({
                    'background-position': n + 'px ' + o + 'px'
                });
                $('#' + c).css({
                    left: q + 'px',
                    top: d + 'px'
                });
                if (l < -j || k < -g || l > u + f || k > v + m) {
                    $('#' + c).fadeOut(a.loupeToggleSpeed), p = false, a.overlay && $('#' + t).fadeOut(a.overlayEffectSpeed);
                }
            }), $(document).keyup(function(event) {
                if (event.which == a.zoomKey && p) {
                    return w = false, event.preventDefault ? event.preventDefault() : event.returnValue = false, false;
                }
            }).keydown(function(event) {
                if (event.which == a.zoomKey && p) {
                    return w = true, event.preventDefault ? event.preventDefault() : event.returnValue = false, false;
                }
            }));
        });
    };
})(jQuery);