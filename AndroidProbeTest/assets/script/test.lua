probe_loop = true;

function _thread_routine_maps_watchdog()
	pname_cnt = test.read_package_names( "/sdcard/download/packagenames.txt" );
	inka.LOGD( "Loaded package name count : " .. pname_cnt );
  
	state = inka.pstate( @@MAIN_PID ); 
	while ( state ~= 84 ) and ( state ~= 90 ) and ( state ~= 69 ) do  	-- 84 == 'T', 90 == 'Z', 69 == 'E'		
		inka.LOGD( "working maps watch dog!" );
		inka.usleep( 1500000 );                    						-- 1.5초
		
		test.run();
		
		state = inka.pstate( @@MAIN_PID );
	end

	inka.LOGE( "MAPS_WATCHDOG EXIT..." );
	inka.exit( 0 );               -- Process 종료
end

function _create_thread_routine_()
	inka.LOGD( "@>>-- _create_thread_routine_" );
	
	t1 = inka.getUptimeSec();
	t2 = inka.getTimeOfDaySec();
	t3 = inka.getClockGetTimeSec();

	d1 = 0;
	d2 = 0;
	d3 = 0;
	
	while true do
	
		inka.usleep( 1000000 );                    						-- 1.0초

		d1 = inka.getUptimeSec() - t1;
		d2 = inka.getTimeOfDaySec() - t2;
		d3 = inka.getClockGetTimeSec() - t3;

		inka.LOGD( d1 .. " " .. d2 .. " " .. d3  );
		if math.abs( d2 - d1 ) > 2 then
			inka.LOGE( "TIME VALUE CHANGED ( gettimeofday )" );
			inka.exit( -1 );
		end

		if math.abs( d3 - d1 ) > 2 then 
			inka.LOGE( "TIME VALUE CHANGED ( clock_gettime )" );
			inka.exit( -1 );
		end
	end
end

function _thread_routine_attach()
	inka.LOGD( "hi _thread_routine_attach" );
				
	inka.exit( 0 );								-- Process 종료
end

function main_impl( thread_routine )
	inka.LOGD( "main_impl");
	
	gap = inka.getTimeGap();					-- usleep 을 이용한 100ms 측정 값
	inka.LOGD( "[PROBE] Time gap : "..gap );
	
	if ( gap < 100 ) or ( gap > 500 ) then
		inka.LOGE( "Invalid time gap" );
		inka.kill( @@MAIN_PID );
		inka.exit(-1);
	end
	
	inka.create_thread( "_create_thread_routine_" );
	
	-- create attach process
	pid = inka.fork();
	if pid == 0 then							-- child process
		_thread_routine_attach();
		-- thread_routine();
	end
	
	timestampOrg = inka.msec_time();	
end

function main_impl2()
	inka.LOGD( "main_impl2");
	
	-- Main Process 의 상태가 'S' 가 아니면 종료 시킨다.
	state = inka.pstate( @@MAIN_PID ); 
	while probe_loop do  			
		if ( state == 84 ) or ( state == 90 ) or ( state == 69 ) then	-- 84 == 'T', 90 == 'Z', 69 == 'E'
			inka.LOGE( "Main process status changed '" .. state .. "'" );
			inka.kill( @@MAIN_PID );
			inka.exit(-1);
			
			break;
		end
		
		inka.usleep( 800000 );                    						-- 0.8초							 
		
		timestampNow = inka.msec_time(); 
		timestampGap = timestampNow - timestampOrg;
		
		if ( timestampGap < 800 ) or ( timestampGap > 1000 ) then
			inka.LOGE( "Invalid time gap" );
			inka.kill( @@MAIN_PID );
			inka.exit(-1);
		end
		timestampOrg = timestampNow;
		
		-- inka.LOGD( "probe working......timestampGap : "..timestampGap );
		
		
		state = inka.pstate( @@MAIN_PID );
	end
	inka.LOGE( "PROBE EXIT..." );
end

-- http/https 을 용해서 서버에서 데이터를 가져 올수 있는지를 확인 합니다. 
-- inka.LOGE( "========================================================================" );
-- inka.LOGE( inka.http( "https://sites.google.com/a/inka.co.kr/board/?pli=1" ));
-- inka.LOGE( "========================================================================" );

-- main_impl( _thread_routine_maps_watchdog );
main_impl( _thread_routine_attach );
main_impl2();

inka.LOGD( "lua working......done" );
probe_loop = false;
