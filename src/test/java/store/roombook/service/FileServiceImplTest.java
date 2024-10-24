package store.roombook.service;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import net.coobird.thumbnailator.Thumbnails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import store.roombook.FileStorageProperties;
import store.roombook.dao.FileDao;
import store.roombook.domain.FileDto;
import store.roombook.domain.FileServiceResult;

import java.io.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class})
@ContextConfiguration(locations = "file:web/WEB-INF/spring/**/testContext.xml")
class FileServiceImplTest {

    @InjectMocks
    FileService fileService = new FileServiceImpl();

    @Mock
    FileDao fileDao;
    @Mock
    FileStorageProperties fileStorageProperties;
    @Mock
    MultipartFile file;
    @Mock
    Thumbnails.Builder<File> mockThumbnailBuilder;
    @Mock
    PngImage mockPngImage;
    @Mock
    PngOptimizer pngOptimizer;
    @Mock
    ByteArrayOutputStream byteArrayOutputStream;

    @Nested
    class SaveFilesTest{
        @Nested
        class NewFileTest{
            @BeforeEach
            void setUp() {
                MockitoAnnotations.openMocks(this);
            }

            @Test
            @DisplayName("파일이 null일 때")
            void isNullTest() throws IOException {
                FileServiceResult result = fileService.saveFiles(null, 1234, "admin");
                assertTrue(result.isSaved());
            }

            @Test
            @DisplayName("파일이 empty일 때")
            void isEmptyTest() throws IOException {
                FileServiceResult result = fileService.saveFiles(new MultipartFile[0], 1234, "admin");
                assertTrue(result.isSaved());
            }

            @Test
            @DisplayName("유효하지 않은 파일 확장자일 때")
            void invalidFileTest() {
                when(fileDao.checkExceedingMaxFileCnt(any(FileDto.class))).thenReturn(1);
                when(file.getOriginalFilename()).thenReturn("fileName.jfif");

                assertThrows(MultipartException.class, () -> fileService.saveFiles(new MultipartFile[]{file}, 1234, "admin"));
            }

//            @Test
//            @DisplayName("파일이 png 확장자일 때")
//            void validFileTest() throws Exception {
//                when(fileDao.checkExceedingMaxFileCnt(any(FileDto.class))).thenReturn(1);
//                when(file.getOriginalFilename()).thenReturn("fileName.png");
//                doNothing().when(file).transferTo(any(File.class));
//                when(fileDao.insertFiles(ArgumentMatchers.any())).thenReturn(1);
//
//                mockStatic(Thumbnails.Builder.class);
//                mockStatic(Thumbnails.class);
//                when(Thumbnails.of(any(File.class))).thenReturn(mockThumbnailBuilder);
//                when(mockThumbnailBuilder.crop(Positions.CENTER)).thenReturn(mockThumbnailBuilder);
//                when(mockThumbnailBuilder.size(anyInt(), anyInt())).thenReturn(mockThumbnailBuilder);
//                when(mockThumbnailBuilder.outputFormat(anyString())).thenReturn(mockThumbnailBuilder);
//                Mockito.mockConstruction(PngImage.class).constructed();
//                PowerMockito.whenNew(PngImage.class)
//                        .withParameterTypes(InputStream.class, String.class)
//                        .withArguments(any(InputStream.class), anyString())
//                        .thenReturn(mockPngImage);
//                when(mockPngImage.getFileName()).thenReturn("asd");
//
//
//                doNothing().when(mockPngImage).setFileName(anyString());
//
//                PowerMockito.whenNew(PngOptimizer.class).withNoArguments().thenReturn(pngOptimizer);
//                doNothing().when(pngOptimizer).optimize(any(PngImage.class), anyString(), anyBoolean(), anyInt());
//
//                FileServiceResult result = fileService.saveFiles(new MultipartFile[]{file}, 1234, "admin");
//                assertTrue(result.isSaved());
//            }
        }
    }
}