package com.goren4u.phpure;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;


public class FunctionPurityTest
{
	private ArrayList<String> _PHPpureFuncs;
	private ArrayList<FunctionDetails> _functions;
	
	public FunctionPurityTest(ArrayList<FunctionDetails> funcs)
	{
		_functions=funcs;
		purgeImpure();
		_PHPpureFuncs=new ArrayList<String>(Arrays.asList(pureBuiltInPHP()));
		for (int i=0;i<_functions.size();i++)
		{
			FunctionDetails fd=_functions.get(i);
			fd.isPure=InitialScreening(fd)?0:-1;
		}
		purgeImpure();
		int last_size=_functions.size()+1;
		while (last_size>_functions.size())
		{
			for (FunctionDetails fd: _functions)
			{
				for (String FuncName : fd.functionCalls)
				{
					if (!isFuncNameFamiliar(FuncName))
						fd.isPure=-1;
				}
			}
			last_size=_functions.size();
			purgeImpure();
		}
	}
	
	public  ArrayList<FunctionDetails> getFunctionList()	{ return _functions;}
	public  ArrayList<File> getFileList()
	{
		String[] filesWithPureFuns= DistinctFileNames();
		ArrayList<File> ret=new ArrayList<File>();
		for (String filePath : filesWithPureFuns)
		{
			ret.add(new File(filePath));
		}
		return ret;
	}
	
	private String[] DistinctFileNames()
	{
		TreeSet<String> set=new TreeSet<String>();
		for (int i=0;i<_functions.size();i++)
		{
			set.add(_functions.get(i).FilePath);	
		}
		String[] ret=new String[set.size()];
		set.toArray(ret);
		return ret;
	}
	
	private boolean isFuncNameFamiliar(String FuncName)
	{
		if (_PHPpureFuncs.contains(FuncName))
			return true;
		for (int i=0;i<_functions.size();i++)
		{
			if (_functions.get(i).equals(FuncName))
				return true;
		}
		return false;
	}
	
	private boolean InitialScreening(FunctionDetails fd)
	{
		if (fd.Args.contains("&"))//by ref
			return false;
		if (fd.Args.length()>0 && !fd.Args.contains("$"))//javascript
			return false;		
		if (fd.Body.contains("global "))//globals
			return false;
		if (fd.Body.contains("$GLOBALS"))//globals
			return false;
		/*
		if (fd.Body.contains("$_"))//superglobals $_POST,etc
			return false;
		*/
		if (fd.Body.contains("$this"))//a member of a class that uses state
			return false;
		if (fd.Body.contains("mysql"))
			return false;
		if (fd.Body.contains("sock"))
			return false;
		if (fd.Body.contains("curl"))
			return false;
		if (fd.Body.contains("fopen"))
			return false;
		return true;
	}
	
	private void purgeImpure()
	{
		ArrayList<FunctionDetails> ret = new ArrayList<FunctionDetails>();
		for (int i=0;i<_functions.size();i++)
		{
			if (_functions.get(i).isPure>=0)
				ret.add(_functions.get(i));
		}
		_functions=ret;
	}
	
	
	private String[] pureBuiltInPHP()
	{
		//TODO: Read from file, maybe add wordpress functions
		String[] ret= {"zend_version","echo","func_num_args","func_get_arg","func_get_args","strlen","strcmp","strncmp","strcasecmp",
				"strncasecmp","each","error_reporting","define","defined","get_class","get_called_class","get_parent_class","method_exists",
				"property_exists","class_exists","interface_exists","trait_exists","function_exists","class_alias","get_included_files",
				"get_required_files","is_subclass_of","is_a","get_class_vars","get_object_vars","get_class_methods","trigger_error",
				"user_error","restore_error_handler","restore_exception_handler","get_declared_classes","get_declared_traits",
				"get_declared_interfaces","get_defined_functions","get_defined_vars","create_function","get_resource_type",
				"get_loaded_extensions","extension_loaded","get_extension_funcs","get_defined_constants","debug_backtrace",
				"debug_print_backtrace","gc_collect_cycles","gc_enabled","gc_enable","gc_disable","bcadd","bcsub","bcmul","bcdiv",
				"bcmod","bcpow","bcsqrt","bcscale","bccomp","bcpowmod","jdtogregorian","gregoriantojd","jdtojulian","juliantojd",
				"jdtojewish","jewishtojd","jdtofrench","frenchtojd","jddayofweek","jdmonthname","easter_date","easter_days","unixtojd",
				"jdtounix","cal_to_jd","cal_from_jd","cal_days_in_month","cal_info","ctype_alnum","ctype_alpha","ctype_cntrl","ctype_digit",
				"ctype_lower","ctype_graph","ctype_print","ctype_punct","ctype_space","ctype_upper","ctype_xdigit","strtotime","date",
				"idate","gmdate","mktime","gmmktime","checkdate","strftime","gmstrftime","time","localtime","getdate","date_create",
				"date_create_from_format","date_parse","date_parse_from_format","date_get_last_errors","date_format","date_modify",
				"date_add","date_sub","date_timezone_get","date_diff","date_timestamp_get","timezone_open","timezone_name_get",
				"timezone_name_from_abbr","timezone_transitions_get","timezone_location_get","timezone_identifiers_list",
				"timezone_abbreviations_list","timezone_version_get","date_interval_create_from_date_string","date_interval_format",
				"date_default_timezone_get","date_sunrise","date_sun_info","ereg","ereg_replace","eregi","eregi_replace","split","spliti",
				"sql_regcase","filter_input","filter_var","filter_input_array","filter_var_array","filter_list","filter_has_var","filter_id",
				"hash","hash_file","hash_hmac","hash_hmac_file","hash_init","hash_update","hash_update_stream","hash_update_file",
				"hash_final","hash_copy","hash_algos","mhash_keygen_s2k","mhash_get_block_size","mhash_get_hash_name","mhash_count","mhash",
				"iconv","iconv_get_encoding","iconv_strlen","iconv_substr","iconv_strpos","iconv_strrpos","iconv_mime_encode",
				"iconv_mime_decode","iconv_mime_decode_headers","json_encode","json_decode","json_last_error","mcrypt_ecb","mcrypt_cbc",
				"mcrypt_cfb","mcrypt_ofb","mcrypt_get_key_size","mcrypt_get_block_size","mcrypt_get_cipher_name","mcrypt_create_iv",
				"mcrypt_list_algorithms","mcrypt_list_modes","mcrypt_get_iv_size","mcrypt_encrypt","mcrypt_decrypt","mcrypt_module_open",
				"mcrypt_generic_init","mcrypt_generic","mdecrypt_generic","mcrypt_generic_end","mcrypt_generic_deinit","mcrypt_enc_self_test",
				"mcrypt_enc_is_block_algorithm_mode","mcrypt_enc_is_block_algorithm","mcrypt_enc_is_block_mode","mcrypt_enc_get_block_size",
				"mcrypt_enc_get_key_size","mcrypt_enc_get_supported_key_sizes","mcrypt_enc_get_iv_size","mcrypt_enc_get_algorithms_name",
				"mcrypt_enc_get_modes_name","mcrypt_module_self_test","mcrypt_module_is_block_algorithm_mode",
				"mcrypt_module_is_block_algorithm","mcrypt_module_is_block_mode","mcrypt_module_get_algo_block_size",
				"mcrypt_module_get_algo_key_size","mcrypt_module_get_supported_key_sizes","mcrypt_module_close","preg_match",
				"preg_match_all","preg_replace","preg_replace_callback","preg_filter","preg_split","preg_quote","preg_grep","session_id",
				"spl_classes","spl_autoload","spl_autoload_extensions","spl_autoload_register","spl_autoload_unregister",
				"spl_autoload_functions","spl_autoload_call","class_parents","class_implements","class_uses","spl_object_hash",
				"iterator_to_array","iterator_count","iterator_apply","constant","bin2hex","hex2bin","sleep","usleep","time_nanosleep",
				"time_sleep_until","flush","wordwrap","htmlspecialchars","htmlentities","html_entity_decode","htmlspecialchars_decode",
				"get_html_translation_table","sha1","sha1_file","md5","md5_file","crc32","iptcparse","iptcembed","getimagesize",
				"getimagesizefromstring","image_type_to_mime_type","image_type_to_extension","phpinfo","phpversion","phpcredits",
				"php_logo_guid","php_real_logo_guid","php_egg_logo_guid","zend_logo_guid","php_sapi_name","php_uname",
				"php_ini_scanned_files","php_ini_loaded_file","strnatcmp","strnatcasecmp","substr_count","strspn","strcspn","strtok",
				"strtoupper","strtolower","strpos","stripos","strrpos","strripos","strrev","hebrev","hebrevc","nl2br","basename","dirname",
				"pathinfo","stripslashes","stripcslashes","strstr","stristr","strrchr","str_shuffle","str_word_count","str_split","strpbrk",
				"substr_compare","strcoll","substr","substr_replace","quotemeta","ucfirst","lcfirst","ucwords","strtr","addslashes",
				"addcslashes","rtrim","str_replace","str_ireplace","str_repeat","count_chars","chunk_split","trim","ltrim","strip_tags",
				"similar_text","explode","implode","join","localeconv","soundex","levenshtein","chr","ord","parse_str","str_getcsv",
				"str_pad","chop","strchr","sprintf","printf","vprintf","vsprintf","fprintf","vfprintf","sscanf","fscanf","parse_url",
				"urlencode","urldecode","rawurlencode","rawurldecode","http_build_query","readlink","linkinfo","symlink","link","unlink",
				"exec","system","escapeshellcmd","escapeshellarg","passthru","shell_exec","proc_open","proc_close","proc_terminate",
				"proc_get_status","getrandmax","mt_getrandmax","getservbyname","getservbyport","getprotobyname","getprotobynumber",
				"getmyuid","getmygid","getmypid","getmyinode","getlastmod","base64_decode","base64_encode","convert_uuencode",
				"convert_uudecode","abs","ceil","floor","round","sin","cos","tan","asin","acos","atan","atanh","atan2","sinh","cosh","tanh",
				"asinh","acosh","expm1","log1p","pi","is_finite","is_nan","is_infinite","pow","exp","log","log10","sqrt","hypot","deg2rad",
				"rad2deg","bindec","hexdec","octdec","decbin","decoct","dechex","base_convert","number_format","fmod","inet_ntop",
				"inet_pton","ip2long","long2ip","getenv","putenv","getopt","microtime","gettimeofday","uniqid","quoted_printable_decode",
				"quoted_printable_encode","convert_cyr_string","get_current_user","header_register_callback","get_cfg_var",
				"magic_quotes_runtime","get_magic_quotes_gpc","get_magic_quotes_runtime","error_log","error_get_last","serialize",
				"unserialize","var_dump","var_export","debug_zval_dump","print_r","memory_get_usage","memory_get_peak_usage",
				"register_shutdown_function","register_tick_function","unregister_tick_function","highlight_file","show_source",
				"highlight_string","php_strip_whitespace","get_include_path","restore_include_path","header","header_remove",
				"headers_sent","headers_list","http_response_code","connection_aborted","connection_status","ignore_user_abort",
				"parse_ini_file","parse_ini_string","is_uploaded_file","move_uploaded_file","gethostbyaddr","gethostbyname","gethostbynamel",
				"gethostname","dns_check_record","checkdnsrr","dns_get_mx","getmxrr","dns_get_record","intval","floatval","doubleval",
				"strval","gettype","is_null","is_resource","is_bool","is_long","is_float","is_int","is_integer","is_double","is_real",
				"is_numeric","is_string","is_array","is_object","is_scalar","is_callable","umask","getcwd","rewinddir","readdir","dir",
				"scandir","glob","fileatime","filectime","filegroup","fileinode","filemtime","fileowner","fileperms","filesize","filetype",
				"file_exists","is_writable","is_writeable","is_readable","is_executable","is_file","is_dir","is_link","stat","lstat","chown",
				"chgrp","chmod","touch","clearstatcache","disk_total_space","disk_free_space","diskfreespace","realpath_cache_size",
				"realpath_cache_get","mail","ezmlm_hash","openlog","syslog","closelog","lcg_value","metaphone","ob_start","ob_flush",
				"ob_clean","ob_end_flush","ob_end_clean","ob_get_flush","ob_get_clean","ob_get_length","ob_get_level","ob_get_status",
				"ob_get_contents","ob_implicit_flush","ob_list_handlers","ksort","krsort","natsort","natcasesort","asort","arsort","sort",
				"rsort","usort","uasort","uksort","shuffle","array_walk","array_walk_recursive","count","end","prev","next","current","key",
				"min","max","in_array","array_search","extract","compact","array_fill","array_fill_keys","range","array_multisort",
				"array_push","array_pop","array_shift","array_unshift","array_splice","array_slice","array_merge","array_merge_recursive",
				"array_replace","array_replace_recursive","array_keys","array_values","array_count_values","array_reverse","array_reduce",
				"array_pad","array_flip","array_change_key_case","array_rand","array_unique","array_intersect","array_intersect_key",
				"array_intersect_ukey","array_uintersect","array_intersect_assoc","array_uintersect_assoc","array_intersect_uassoc",
				"array_uintersect_uassoc","array_diff","array_diff_key","array_diff_ukey","array_udiff","array_diff_assoc",
				"array_udiff_assoc","array_diff_uassoc","array_udiff_uassoc","array_sum","array_product","array_filter","array_map",
				"array_chunk","array_combine","array_key_exists","pos","sizeof","key_exists","assert","assert_options","version_compare",
				"str_rot13","token_get_all","token_name","utf8_encode","utf8_decode","apache_lookup_uri","virtual","apache_request_headers",
				"apache_response_headers","apache_getenv","apache_note","apache_get_version","apache_get_modules","getallheaders","gd_info",
				"mb_convert_case","mb_strtoupper","mb_strtolower","mb_language","mb_internal_encoding","mb_http_input","mb_http_output",
				"mb_detect_order","mb_substitute_character","mb_parse_str","mb_output_handler","mb_preferred_mime_name","mb_strlen",
				"mb_strpos","mb_strrpos","mb_stripos","mb_strripos","mb_strstr","mb_strrchr","mb_stristr","mb_strrichr","mb_substr_count",
				"mb_substr","mb_strcut","mb_strwidth","mb_strimwidth","mb_convert_encoding","mb_detect_encoding","mb_list_encodings",
				"mb_encoding_aliases","mb_convert_kana","mb_encode_mimeheader","mb_decode_mimeheader","mb_convert_variables",
				"mb_encode_numericentity","mb_decode_numericentity","mb_send_mail","mb_get_info","mb_check_encoding","mb_regex_encoding",
				"mb_ereg","mb_eregi","mb_ereg_replace","mb_eregi_replace","mb_ereg_replace_callback","mb_split","mb_ereg_match",
				"mb_ereg_search","mb_ereg_search_pos","mb_ereg_search_regs","mb_ereg_search_init","mb_ereg_search_getregs",
				"mb_ereg_search_getpos","mbregex_encoding","mbereg","mberegi","mbereg_replace","mberegi_replace","mbsplit","mbereg_match",
				"mbereg_search","mbereg_search_pos","mbereg_search_regs","mbereg_search_init","mbereg_search_getregs","mbereg_search_getpos"};
		return ret;
	}
}
