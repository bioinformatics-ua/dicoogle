/*
 * Copyright (C) 2015  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-webcore.
 *
 * Dicoogle/dicoogle-webcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-webcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */

(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.dicoogleClient=f()}})(function(){var define,module,exports;return function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s}({1:[function(require,module,exports){/**
 * Dicoogle Service Wrapper
 */
/** @namespace */
var dicoogle=function dicoogle_module(){
// private variables of the module
var url_="http://localhost:8080";
// module
var m={};var EndPoints={SEARCH:"search",PROVIDERS:"providers"};m.Endpoints=EndPoints;/** Perform a raw request.
   * @param service the URI endpoint of the service
   * @param qs the query string of the request
   * @param callback (error, result)
   */
m.request=function dicoogle_queryFreeText(service,qs,callback){service_request("GET",service,qs,function(err,data){callback(err,data?data:null)})};/** Perform a free text query.
   * @param query text query
   * @param callback (error, result)
   */
m.queryFreeText=function dicoogle_queryFreeText(query,callback){service_request("GET",EndPoints.SEARCH,{keyword:false,query:query},function(err,data){callback(err,data?data.results:null)})};/** Perform an advanced query.
   * @param query text query
   * @param callback (error, result)
   */
m.queryAdvanced=function dicoogle_queryAdvanced(query,callback){service_request("GET",EndPoints.SEARCH,{keyword:true,query:query},function(err,data){callback(err,data?data.results:null)})};
//---------------------private methods--------------------------
function isArray(it){var ostring=Object.prototype.toString;return ostring.call(it)==="[object Array]"}function parseUrl(uri,qs){
// create full query string
var end_url=url_;if(isArray(qs[uri])){end_url+=uri.join("/")}else{end_url+=uri}var qstring;if(!qs){qstring=""}if(typeof qs==="string"){qstring="?"+qs}else{var qparams=[];for(var pname in qs){if(isArray(qs[pname])){for(var j=0;j<qs[pname].length;j++){qparams.push(pname+"="+encodeURIComponent(qs[pname][j]))}}else if(qs[pname]){qparams.push(pname+"="+encodeURIComponent(qs[pname]))}else{qparams.push(pname)}}qstring="?"+qparams.join("&")}return end_url+qstring}/**
   * send a REST request to the service
   *
   * @param {string} method the http method ('GET','POST','PUT' or 'DELETE')
   * @param {string} uri the request URI
   * @param {string|hash} qs the query string parameters
   * @param {Function(error,outcome)} callback
   */
var service_request=function(method,uri,qs,callback){var end_url=parseUrl(uri,qs);
// This XDomainRequest thing is for IE support (lulz)
var req=typeof XDomainRequest!=="undefined"?new XDomainRequest:new XMLHttpRequest;req.onreadystatechange=function(){if(req.readyState===4){if(req.status!==200){callback({code:"SERVER-"+req.status,message:req.statusText},null);return}var type=req.getResponseHeader("Content-Type");var mime=type;if(mime.indexOf(";")!==-1){mime=mime.split(";")[0]}var result;if(mime==="application/json"){result=JSON.parse(req.responseText);callback(null,result)}else{result={type:type,text:req.responseText};callback(null,result)}}};req.open(method,end_url,true);req.send()};/**
   * Initialize a new Dicoogle access object, which can be used multiple times.
   *
   * @param {String} url the controller service's base url
   * @return a dicoogle service access object
   */
return function(url){url_=url||"http://localhost:8080/";if(url_[url_.length-1]!=="/")url_+="/";if(url_.indexOf("://")===-1){url_="http://"+url_}return m}}();module.exports=dicoogle},{}]},{},[1])(1)});
//# sourceMappingURL=dicoogle-client.js.map