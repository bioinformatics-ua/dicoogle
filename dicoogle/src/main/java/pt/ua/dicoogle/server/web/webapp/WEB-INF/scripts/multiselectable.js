/*globals $*/
/*
 * Multiselectable jQuery plugin
 * A Progressive Enhancement to <select multiple>
 * Copyright Aki Björklund, 2009–2011
 * http://akibjorklund.com/code/multiselectable/
 * Version 1.0.1
 * Released under the MIT license.
 */
(function ($) {
	$.fn.multiselectable = function (options) {
		var that = $(this);
		options = $.extend({
			template: '<div class="multiselectable">' +
				'<div class="m-selectable-from"><label for="m-selectable"></label>' +
				'<select multiple="multiple" id="m-selectable"></select>' +
				'</div>' +
				'<div class="m-selectable-controls">' +
					'<button class="multis-right"></button>' +
					'<button class="multis-left"></button>' +
				'</div>' +
				'<div class="m-selectable-to"><label for="m-selected"></label>' +
				'<select multiple="multiple" id="m-selected"></select>' +
				'</div>' +
			'</div>',
			selectableLabel: 'Selectable:',
			selectedLabel: 'Selected:',
			moveRightText: '→',
			moveLeftText: '←',
			sort: true,
			sortAscending: true
		}, options || {});
		
		//disable sorting if sortOptions plugin is not found
		if (!$.fn.sortOptions) {
			options.sort = false;
		}
		
		return that.each(function () {
			var master = $(this);
			
			//make sure there are no duplicate id:s inserted into the document
			var template = options.template;
			var num = 1;
			if ($('#m-selectable').length > 0) {
				while ($('#m-selectable_' + num).length > 0) {
					num++;
				}
				template = template.replace(/\"m-selectable\"/g, 'm-selectable_' + num);
				template = template.replace(/\"m-selected\"/g, 'm-selected_' + num);
			}
			
			//set the template
			master.hide().after(template);
			
			var size = master.attr('size');
			var m = master.next();
			var m1 = m.find('.m-selectable-from select');
			var m2 = m.find('.m-selectable-to select');
			
			//match the size of the reference element
			m1.attr('size', size);
			m2.attr('size', size);			
			
			//set texts according to options 
			m.find('.m-selectable-from label').text(options.selectableLabel);
			m.find('.m-selectable-to label').text(options.selectedLabel);
			m.find('.multis-right').text(options.moveRightText);
			m.find('.multis-left').text(options.moveLeftText);
			
			//move selected options to m2, unselected to m1
			$(this).find('option:selected').clone().appendTo(m2);
			$(this).find('option:not(:selected)').clone().appendTo(m1);
			
			//do an initial sort to both selects
			if (options.sort) {
				m1.sortOptions(options.sortAscending);
				m2.sortOptions(options.sortAscending);
			}
			
			function move(from, to, master, select) {
				from.find('option:selected').removeAttr('selected').remove().appendTo(to).each(function () {
					var matchedElem = master.find('option[value="' + $(this).val() + '"]');
					if (select) {
						matchedElem.attr('selected', 'selected');
					} else {
						matchedElem.removeAttr('selected');
					}
				});
				if (options.sort) {
					to.sortOptions(options.sortAscending);
				}
				return false;
			}
		
			function moveLeft() {
				return move(m2, m1, master, false);
			}
			function moveRight() {
				return move(m1, m2, master, true);
			}
			
			//set all the events that trigger a move
			m.find('.multis-right').click(moveRight);
			m.find('.multis-left').click(moveLeft);
			m1.dblclick(moveRight);  
			m2.dblclick(moveLeft);
			m1.keydown(function (event) {
				if (event.keyCode === 13) {
					moveRight();
				}
			});
			m2.keydown(function (event) {
				if (event.keyCode === 13) {
					moveLeft();
				}
			});
		});
	};
})(jQuery);