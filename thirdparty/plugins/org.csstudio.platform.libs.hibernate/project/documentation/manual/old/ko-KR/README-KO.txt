[NOTICE]FOR Korean,
	�� ���������� ���� ���� �ּ��� ���� ������ ���� �״�� �ξ����ϴ�. 
	������ ���� �κп� �ѱ� ��Ʈ�� ������ ��쿡, �ؽ�Ʈ���� �ϱ׷����� ������ 
	�����ϱ⿡ ������ ������ ���� �ּ��� ��� �ѹ���׽��ϴ�. 
	���Ŀ� �ѱ� ��Ʈ�� �µ��� ������ �������ؾ��� �κ��̴� ���عٶ��ϴ�.
	�׷� ���̹�����Ʈ�� ��ſ� �ð��� �����ñ� �ٶ��ϴ�. 
	
	������ ������(jdkim528@korea.com)
	Blog : http://blog.naver.com/jdkim528/

[Down/Config/Build]
 * ������ CVS���� SVN���� �ٲ�鼭 �ű� ����ڵ��� ���� �����̸� �ۼ��� �ʿ䰡 
     ����׿�... 
  
    ���ڴ� ���������� TortoiseSVN�� Subclipse�� ����ϰ� ������ Subclpse�� �߽����� 
    �����ұ� �մϴ�.(��ȣ�ϴ� svn Ŭ���̾�Ʈ�� �ִٸ� �ش� Ŭ���̾�Ʈ�� ������ 
    ���� ������ �Ŀ� ����ϼŵ� �� ���Դϴ�.)
        
        [Subclipse ��ġ]
        
        Subclipse�� ��ġ�ϴ� ����� http://subclipse.tigris.org/install.html �� �����Ͽ� 
        eclipse�� ����>����Ʈ�����>ã�� �� ��ġ �޴��� ���� ���� ��ġ�� �� �ֽ��ϴ�.
        
        [Subclipse Checkout]
        0) Subclipse ��ġ�� �������� ��Ŭ������ ������Ͻʽÿ�.
        1) ��Ŭ������ Perspective ���⿡�� SVN Repository exploring�� ���� ����Ҹ� ����Ͽ� ����ϰų�
           ��Ŭ������ ��Ű�� Ž���⿡�� ���콺 ������ �˾� �޴����� "������Ʈ"�� ������ŵ�ϴ�.
           ���⼭�� "������Ʈ" ���� ����� �����մϴ�.
        2) ��Ŭ������ ��Ű�� Ž���⿡�� ���콺 ������ �˾� �޴����� "������Ʈ"�� Ŭ���Ѵ�
        3) �˾� â�� "SVN" ��忡�� "Checkout Projects from SVN"�� �����Ѵ�
        4) "����" ��ư�� Ŭ���Ѵ�
        5) "Create a new respository location"�� �����Ѵ�
        6) ����" ��ư�� Ŭ���Ѵ�
        7) Location url�� "http://anonhibernate.labs.jboss.com/trunk/Hibernate3" �Ǵ� 
           "https://hibernate.labs.jboss.com/repos/hibernate/trunk" �� �Է��մϴ�.
        8) "Hibernate3" ���� �����ϰų� ������ Ư�� ��带 �����ϰ� "�Ϸ�" ��ư�� Ŭ���Ѵ�.
        9) ������Ʈ ���� hibernate3 ���� ���ϴ� �̸����� ����Ѵ�.
        10) checkout�� ����˴ϴ�.
         
        [TortoiseSVN ��ġ]
	TortoiseSVN Ŭ���̾�Ʈ�� ��ġ�ϼ̴ٸ�, �ý����� ����� �Ͻʽÿ�.
	1)�������丮�� ���� ������ �ϳ� ������ŵ�ϴ�.(D:\repo)
	2)������Ž���⿡�� D:\repo�� ���콺 ������ Ŭ���� ��  TortoiseSVN �޴����� 
	  "Create repository Here..."�� Ŭ���ϸ� �˾��� �ߴµ�, 
	   ���Ͻý���/��Ŭ��DB ���� �� �ϳ���  �����ϰ� OK ��ư�� ��������.
	3)hibernate�� ���� �ޱ� ���� ������ �ϳ� ������Ű����(D:\repo\Hibernate3)
	4)D:\repo\hibernate ������ ���콺 ������ Ŭ���� ��, 
	  TortoiseSVN �˾� �޴����� CheckOut�� Ŭ���Ͻʽÿ�.
	5)URL repository�� "http://anonhibernate.labs.jboss.com/trunk/Hibernate3" �Ǵ� 
           "https://hibernate.labs.jboss.com/repos/hibernate/trunk" �� �Է��ϰ�, 
	  OK ��ư�� Ŭ���Ͻʽÿ�
	6)��� ���������� ������ D:\repo\Hibernate3\doc\reference�� �̵��մϴ�.
	7)���� �����Ͻø� �˴ϴ�.
	
	*) �ѱ� �������� �ʿ��Ͻôٸ� ������ ���� �ϼŵ� �˴ϴ�.
	/doc/reference/build.xml ������ 
	�Ʒ� ���� ���� �ѱ� ������ ���� �κе��� �ּ�ó���մϴ�. 
	[��]. �����ϱ�
	�׷� ����  [���� ���]/reference/ ���� ant all.doc�� �����Ͻø� �˴ϴ�.
	���� �ð��� 2�� ���� �ҿ�˴ϴ�.
	[��]. ���� ����
	���丮 [���� ���]/reference/build/ko/ ���丮�� ����� ������ ���ñ� �ٶ��ϴ�.
	�׷� ���̹�����Ʈ�� �Բ� ��ſ� �ð��� ��������.
	
	[��]
	    <target name="all.doc"
            depends="clean"
            description="Compile documentation for all languages and all formats.">

        <!-- TRANSLATOR: Duplicate this line for your language -->
        <!--antcall target="lang.all"><param name="lang" value="en"/></antcall-->
        <!--antcall target="lang.all"><param name="lang" value="zh-cn"/></antcall-->
        <!--antcall target="lang.all"><param name="lang" value="es"/></antcall-->
    	<antcall target="lang.all"><param name="lang" value="ko"/></antcall>

    </target>
    <target name="all.revdiff"
            description="Generates a diff report for all translated versions.">

        <!-- TRANSLATOR: Duplicate this line for your language -->
        <!--antcall target="lang.revdiff"><param name="lang" value="zh-cn"/></antcall-->
        <!--antcall target="lang.revdiff"><param name="lang" value="es"/></antcall-->
    	<antcall target="lang.revdiff"><param name="lang" value="ko"/></antcall>

    </target>
