/*
 * MapsResder.cpp
 *
 *  Created on: 2016. 4. 7.
 *      Author: purehero2
 */

#include <MapsReader.h>
#include <string.h>

void MAPS_DATA::print() const
{
	printf("%08llx-%08llx %4s %8llx %s %8d %s\n",startAddr,endAddr,permission,unknown1,unknown2,unknown3,filename);
}

MapsReader::MapsReader( int pid )
: m_pid( pid )
{}
MapsReader::~MapsReader() {}

/*
 * m_pid �� maps ������ �о �о�� ������ ������ ��ȯ�մϴ�.
 * ���� �ÿ� �������� ��ȯ
 * */
int MapsReader::read_maps_infos()
{
	char strTemp[512];
	sprintf( strTemp,"/proc/%d/maps", m_pid );

	FILE * maps_fp = fopen (strTemp,"rb");
	if( maps_fp == NULL)
	{
		printf("Failed to open file [%s]\n",strTemp);
		return -1;
	}

	m_maps_infos.clear();

	MAPS_DATA maps_data;
	while( fgets( strTemp, 512, maps_fp ) != NULL )
	{
		sscanf( strTemp,"%llx-%llx %s %llx %s %d %s",
				&maps_data.startAddr,
				&maps_data.endAddr,
				(char*)&maps_data.permission,
				&maps_data.unknown1,
				(char*)&maps_data.unknown2,
				&maps_data.unknown3,
				(char*)&maps_data.filename
		);

		m_maps_infos.push_back( maps_data );
	}

	fclose( maps_fp );

	return (int)m_maps_infos.size();
}

/*
 * Maps �������� filename �� ���Ե� �����͵��� ��ȯ�� �ݴϴ�.
 * */
int MapsReader::find_infos( const char * filename, std::vector<MAPS_DATA*> & result)
{
	result.clear();

	int len = (int) m_maps_infos.size();
	for( int i = 0; i < len; i++ )
	{
		if( strstr( m_maps_infos[i].filename, filename ) != NULL )
		{
			result.push_back( &m_maps_infos[i] );
		}
	}

	return (int)result.size();
}
